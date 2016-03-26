package utilPack;

import java.io.*;

public class MyFile {

    private FileInputStream fin;
    private FileOutputStream fout;
    private boolean readStatus, writeStatus;
    private int currentReadBitPos, currentWriteBitPos;
    private byte currentByte;
    private boolean dirty;

    public MyFile() {
        readStatus = false;
        writeStatus = false;
        currentReadBitPos = currentWriteBitPos = 0;
        currentByte = (byte) 0;
        dirty = false;
    }

    public boolean closeRead() {
        readStatus = false;
        try {
            fin.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int available() {
        int a;
        try {
            a = fin.available();
        } catch (IOException e) {
            a = 0;
            System.out.println("fin error: " + e);
        }
        return a;
    }

    public boolean closeWrite() {

        if (dirty) {
            writeByte(currentByte);
            writeByte((byte) currentWriteBitPos);
        }
        writeStatus = false;

        try {
            fout.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean openRead(String fileName) {
        try {
            fin = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            return false;
        }
        readStatus = true;
        return true;
    }

    public boolean openWrite(String fileName, boolean appendFlag) {
        try {
            fout = new FileOutputStream(fileName, appendFlag);
        } catch (FileNotFoundException e) {
            return false;
        }
        writeStatus = true;
        return true;
    }

    public boolean writeBit(int bit) {
        if (!writeStatus) {
            return false;
        }

        bit &= 1;
        for (int i = 0; i < currentWriteBitPos; i++) {
            bit = bit << 1;
        }

        currentByte |= bit;
        currentWriteBitPos++;
        dirty = true;

        if (currentWriteBitPos == 8) {
            writeByte(currentByte);
            currentByte = 0;
            currentWriteBitPos = 0;
            //dirty = false; not required since file was written using bits
        }
        return true;
    }

    public int readBit() {
        if (!readStatus) {
            return 0;
        }

        if (dirty == false) {
            currentByte = (byte) readByte();
            dirty = true;
        }

        int temp = currentByte;
        for (int i = 0; i < currentReadBitPos; i++) {
            temp = temp >> 1;
        }
        temp = temp & 1;
        currentReadBitPos++;

        if (currentReadBitPos == 8) {
            currentReadBitPos = 0;
            currentByte = (byte) readByte();
        }
        return temp;
    }

    public boolean writeLong(long val) {
        int i;
        byte b;

        if (writeStatus) {
            try {
                for (i = 0; i < 8; i++) {
                    b = (byte) val;
                    fout.write(b);
                    val = val >>> 8;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean writeByte(int ival) {
        byte val;
        val = (byte) ival;
        //System.out.println("Writing: " + val);
        if (writeStatus) {
            try {
                fout.write(val);
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean getReadStatus() {
        try {
            if ((readStatus) && (fin.available() > 0)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public byte readByte() {
        int i;
        byte b;
        if (readStatus) {
            try {
                i = fin.read();
                if (i != -1) {
                    b = (byte) i;
                } else {
                    readStatus = false;
                    b = 0;
                }
                return b;
            } catch (Exception e) {
                readStatus = false;
                return 0;
            }
        } else {
            return 0;
        }
    }

    public long readLong() {
        int i, j;
        byte b[] = new byte[8];
        long l = 0;

        if (readStatus) {
            try {
                //System.out.print("> ");
                for (j = 0; j < 8; j++) {
                    i = fin.read();
                    if (i != -1) {
                        b[j] = (byte) i;
                        //System.out.print(b[j] + " ");
                    } else {
                        readStatus = false;
                        l = 0;
                        break;
                    }
                }

                if (readStatus) {
                    //System.out.print("-");
                    for (i = 7; i >= 0; i--) {
                        //System.out.print(b[i] + " ");
                        l = l << 8;
                        l = l | (((int) b[i]) & 0xff);
                    }
                }
                //System.out.print("---");

                return l;
            } catch (Exception e) {
                readStatus = false;
                return 0;
            }
        } else {
            return 0l;
        }
    }
}
