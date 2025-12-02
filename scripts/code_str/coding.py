

def _code(val: str) -> str:
    length = len(val)
    result = [''] * length
    val11 = val22 = length - 1
    ss = ((2 ^ 5) << 4) ^ (2 << 1)  # 计算 ss = 50
    tt = ((2 ^ 5) << 3) ^ 3          # 计算 tt = 27

    while val11 >= 0:
        # 第一个赋值操作
        result[val11] = chr(((ord(val[val22]) ^ ss) + (1 << 15))%(1<< 16))
        val22 -= 1

        if val22 < 0:
            break

        # 第二个赋值操作
        val11 -= 1  # 相当于Java中的--val11
        result[val22] = chr((((ord(val[val11]) ^ tt) + (1 << 15)))%(1<< 16))

    return ''.join(result)

if __name__ == "__main__":
    while(True):
        a = input()
        print(_code(a))
        break
    import socket
    socket.gethostbyname_ex("host.docker.internal")