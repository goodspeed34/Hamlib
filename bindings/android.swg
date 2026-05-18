/*  Hamlib bindings - Android PTY helper
 *  Copyright (c) 2024 by Stephane Fillod
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

#if defined(SWIGJAVA)

%begin %{
#if !defined(_GNU_SOURCE)
# define _GNU_SOURCE
#endif
%}

%{
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>
%}

%inline %{

int pty_helper_openpty(void) {
    int fd;

    fd = posix_openpt(O_RDWR | O_NOCTTY);
    if (fd < 0)
        return -1;

    if (grantpt(fd) < 0) {
        close(fd);
        return -1;
    }

    if (unlockpt(fd) < 0) {
        close(fd);
        return -1;
    }

    return fd;
}

const char *pty_helper_ptsname(int fd) {
    return ptsname(fd);
}

int pty_helper_dup(int fd) {
    return dup(fd);
}

void pty_helper_close(int fd) {
    close(fd);
}

%}
#endif
