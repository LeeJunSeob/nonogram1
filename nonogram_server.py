#!/usr/bin/python

import socket
from itertools import izip

server_address = "10.0.1.10"
port = 8040
data_file_name = "datas"

client = -1
clientInfo = -1
dataset = []

def make_data (file_name):
    f = open(file_name, 'r')


    struct = []
    struct.append(int(f.readline()[:-1]))
    while True:
        x = []
        y = []

        for i in range (struct[0]):
            x.append(f.readline())
        for j in range (struct[0]):
            y.append(f.readline())

        struct.append (x)
        struct.append (y)

        f.readline();

        dataset.append (struct[:])

        struct = []
        tmp = f.readline()[:-1]
        if (len(tmp) == 0):
            break

        struct.append(int(tmp))

    f.close()


def connect_close ():
    print ("Socket closed in initial point.");
    client.close()
    sock.close()
    sleep(1)


def connect_client ():
    print ("Waiting to connect...");

    try:
        client, clientInfo = sock.accept ()
        print ("Socket connected:", clientInfo);
        sleep(1)
        return True
    except:
        connect_close ()
        return False


def send_data (msg):
    sendmsg = b'\x00'
    sendmsg += chr(len(msg)).encode('utf-8')
    snedmsg += msg.encode('utf-8')
    client.send (msg)
    return True


def gen_row(w, s):
    """Create all patterns of a row or col that match given runs."""
    def gen_seg(o, sp):
        if not o:
            return [[2] * sp]
        return [[2] * x + o[0] + tail
                for x in xrange(1, sp - len(o) + 2)
                for tail in gen_seg(o[1:], sp - x)]

    return [x[1:] for x in gen_seg([[1] * i for i in s], w + 1 - sum(s))]


def deduce(hr, vr):
    """Fix inevitable value of cells, and propagate."""
    def allowable(row):
        return reduce(lambda a, b: [x | y for x, y in izip(a, b)], row)

    def fits(a, b):
        return all(x & y for x, y in izip(a, b))

    def fix_col(n):
        """See if any value in a given column is fixed;
        if so, mark its corresponding row for future fixup."""
        c = [x[n] for x in can_do]
        cols[n] = [x for x in cols[n] if fits(x, c)]
        for i, x in enumerate(allowable(cols[n])):
            if x != can_do[i][n]:
                mod_rows.add(i)
                can_do[i][n] &= x

    def fix_row(n):
        """Ditto, for rows."""
        c = can_do[n]
        rows[n] = [x for x in rows[n] if fits(x, c)]
        for i, x in enumerate(allowable(rows[n])):
            if x != can_do[n][i]:
                mod_cols.add(i)
                can_do[n][i] &= x


    w, h = len(vr), len(hr)
    rows = [gen_row(w, x) for x in hr]
    cols = [gen_row(h, x) for x in vr]
    can_do = map(allowable, rows)

    # Initially mark all columns for update.
    mod_rows, mod_cols = set(), set(xrange(w))

    while mod_cols:
        for i in mod_cols:
            fix_col(i)
        mod_cols = set()
        for i in mod_rows:
            fix_row(i)
        mod_rows = set()

    if all(can_do[i][j] in (1, 2) for j in xrange(w) for i in xrange(h)):
        print "Solution would be unique" # but could be incorrect!
    else:
        print "Solution may not be unique, doing exhaustive search:"

    # We actually do exhaustive search anyway. Unique solution takes
    # no time in this phase anyway, but just in case there's no
    # solution (could happen?).
    out = [0] * h

    def try_all(n = 0):
        if n >= h:
            for j in xrange(w):
                if [x[j] for x in out] not in cols[j]:
                    return 0
            return 1
        sol = 0
        for x in rows[n]:
            out[n] = x
            sol += try_all(n + 1)
        return sol

    try_all()
    return (out)


def solver (struct):
    x = []
    y = []

    if len(x) == 0 and len(y) == 0:
        for i in struct[1]:
            x.append(i[:-1].split(" "))
        for i in x:
            for j in range(len(i)):
                i[j] = int(i[j])

        for i in struct[2]:
            y.append(i[:-1].split(" "))
        for i in y:
            for j in range(len(i)):
                i[j] = int(i[j])

    print "Horizontal runs:", x
    print "Vertical runs:", y
    return deduce(x, y)


def show_gram(m):
    # If there's 'x', something is wrong.
    # If there's '?', needs more work.
    tmp = []
    for x in m:
        tmp.append("".join("x#_?"[i] for i in x))
    return tmp


def main ():
    """
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((server_address, port))
    sock.listen(1)

    connect_client ()

    while connect_client ():
        try:
            while True:
                

                else:
                    print("Disconnected")
                    client.close()
                    sock.close()
                    break
        except:
            print("Closing socket")
            client.close()
            sock.close()
    """
    make_data(data_file_name)
    solved = solver (dataset[1])
    solved = show_gram (solved)

    for i in range(len(solved)):
        print "SEND"+str(i)+": ", solved[i]

main ()

