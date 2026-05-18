/*  Hamlib bindings - Android PTY helper class
 *  Copyright (c) 2026 by Gong Zhile
 *
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.hamlib;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Helper for opening a pseudo-terminal and accessing its master side
 * as Java InputStream/OutputStream.
 *
 * Hamlib wouldn't be able to access serial ports directly without root
 * permission and kernel drivers. However, Android apps could fake a
 * serial port with pty and connect them to USB Serial (usb-serial-for-android)
 * or Bluetooth SPP (BluetoothSocket).
 */
public class pty_helper implements AutoCloseable {
    private final int fd;
    private final String slaveName;
    private FileInputStream in;
    private FileOutputStream out;

    /**
     * Open a new pseudo-terminal.
     * @throws IOException if the PTY cannot be opened
     */
    public pty_helper() throws IOException {
        fd = HamlibJNI.pty_helper_openpty();
        if (fd < 0)
            throw new IOException("Failed to open PTY");
        slaveName = HamlibJNI.pty_helper_ptsname(fd);
    }

    /**
     * Return the slave device path (e.g. "/dev/pts/4").
     */
    public String getSlaveName() {
        return slaveName;
    }

    /**
     * Return an InputStream for reading from the PTY master.
     *
     * The stream uses its own file descriptor (dup'd from the master),
     * so closing the returned stream does not affect the output stream
     * or the PTY itself.
     */
    public synchronized InputStream getInputStream() throws IOException {
        if (in == null) {
            int inFd = HamlibJNI.pty_helper_dup(fd);
            if (inFd < 0)
                throw new IOException("Failed to dup PTY fd for input");
            in = new FileInputStream(newFileDescriptor(inFd));
        }
        return in;
    }

    /**
     * Return an OutputStream for writing to the PTY master.
     *
     * The stream uses its own file descriptor (dup'd from the master),
     * so closing the returned stream does not affect the input stream
     * or the PTY itself.
     */
    public synchronized OutputStream getOutputStream() throws IOException {
        if (out == null) {
            int outFd = HamlibJNI.pty_helper_dup(fd);
            if (outFd < 0)
                throw new IOException("Failed to dup PTY fd for output");
            out = new FileOutputStream(newFileDescriptor(outFd));
        }
        return out;
    }

    /**
     * Close the PTY.  The input and output streams are also closed.
     */
    @Override
    public void close() {
        try { if (in != null) in.close(); } catch (IOException e) { /* ignore */ }
        try { if (out != null) out.close(); } catch (IOException e) { /* ignore */ }
        HamlibJNI.pty_helper_close(fd);
    }

    private static FileDescriptor newFileDescriptor(int fd) {
        try {
            FileDescriptor fdo = new FileDescriptor();
            Class<?> cl = fdo.getClass();
            Field f;
            try {
                f = cl.getDeclaredField("fd");              /* OpenJDK */
            } catch (NoSuchFieldException e) {
                f = cl.getDeclaredField("descriptor");      /* Android */
            }
            f.setAccessible(true);
            if (f.getType() == int.class)
                f.setInt(fdo, fd);
            else if (f.getType() == long.class)
                f.setLong(fdo, fd);
            return fdo;
        } catch (Exception e) {
            throw new RuntimeException("Could not create FileDescriptor", e);
        }
    }
}
