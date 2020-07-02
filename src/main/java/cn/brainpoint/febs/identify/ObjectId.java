/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.brainpoint.febs.identify;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ObjectId no container PID:
 *         a 4-byte value representing the seconds since the Unix epoch,
 *         a 3-byte machineId, and
 *         a 3-byte counter, starting with a random value.
 *
 *         <no container pid, must to use database to manager machineId.>
 *
 */
public final class ObjectId {

    public static final int OBJECT_ID_LENGTH = 12;
    public static final int OBJECT_ID_LENGTH_NOPID = 10;

    private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

    // Use primitives to represent the 5-byte random value.
    private static final int RANDOM_VALUE1;
    private static final short RANDOM_VALUE2;

    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final int timestamp;
    private final int counter;
    private final int randomValue1;
    private final short randomValue2;
    private final boolean noRandomValue2;

    /**
     * Generate a objectID in hex string.
     *
     *         a 4-byte value representing the seconds since the Unix epoch,
     *         a 3-byte machineId, and
     *         a 2-byte pid, and
     *         a 3-byte counter, starting with a random value.
     *
     * @return objectID in hex string
     */
    public static String generateHex(final int machineId, final short pid) {
        ObjectId id = new ObjectId(new Date(), machineId, pid);
        return id.toHexString();
    }

    /**
     * Generate a objectID (no container pid) in hex string.
     *
     *         a 4-byte value representing the seconds since the Unix epoch,
     *         a 3-byte machineId, and
     *         a 3-byte counter, starting with a random value.
     *
     * @return objectID (no container pid) in hex string
     */
    public static String generateHexNoPID(final int machineId) {
        ObjectId id = new ObjectId(new Date(), machineId);
        return id.toHexString();
    }


    private ObjectId(final Date date, final int machineId) {
        if ((machineId & 0xff000000) != 0 || machineId == 0) {
            throw new IllegalArgumentException("The machine identifier must be between 1 and 16777215 (it must fit in three bytes).");
        }

        int inc = NEXT_COUNTER.getAndIncrement();
        if (inc > LOW_ORDER_THREE_BYTES) {
            NEXT_COUNTER.set(0);
            inc = NEXT_COUNTER.getAndIncrement();
        }

        this.timestamp = (int)(date.getTime() / 1000);
        this.counter = inc & LOW_ORDER_THREE_BYTES;
        this.randomValue1 = machineId;
        this.randomValue2 = 0;
        this.noRandomValue2 = true;
    }

    private ObjectId(final Date date, final int machineId, final short pid) {
        if ((machineId & 0xff000000) != 0 || machineId == 0) {
            throw new IllegalArgumentException("The machine identifier must be between 1 and 16777215 (it must fit in three bytes).");
        }

        int inc = NEXT_COUNTER.getAndIncrement();
        if (inc > LOW_ORDER_THREE_BYTES) {
            NEXT_COUNTER.set(0);
            inc = NEXT_COUNTER.getAndIncrement();
        }

        this.timestamp = (int)(date.getTime() / 1000);
        this.counter = inc & LOW_ORDER_THREE_BYTES;
        this.randomValue1 = machineId;
        this.randomValue2 = pid;
        this.noRandomValue2 = false;
    }

    /**
     * Convert to a byte array.  Note that the numbers are stored in big-endian order.
     *
     * @return the byte array
     */
    public byte[] toByteArray() {
        ByteBuffer buffer = null;
        if (this.noRandomValue2) {
            buffer = ByteBuffer.allocate(OBJECT_ID_LENGTH_NOPID);
        }
        else {
            buffer = ByteBuffer.allocate(OBJECT_ID_LENGTH);
        }
        putToByteBuffer(buffer);
        return buffer.array();  // using .allocate ensures there is a backing array that can be returned
    }

    /**
     * Convert to bytes and put those bytes to the provided ByteBuffer.
     * Note that the numbers are stored in big-endian order.
     *
     * @param buffer the ByteBuffer
     * @throws IllegalArgumentException if the buffer is null or does not have at least 12 bytes remaining
     * @since 3.4
     */
    public void putToByteBuffer(final ByteBuffer buffer) {
        if (null == buffer) {
            throw new IllegalArgumentException("buffer is empty");
        }

        if (!(this.noRandomValue2 == false && buffer.remaining() >= OBJECT_ID_LENGTH
                || this.noRandomValue2 == true && buffer.remaining() >= OBJECT_ID_LENGTH_NOPID)) {
            throw new IllegalArgumentException("buffer.remaining() >=12");
        }

        buffer.put(int3(timestamp));
        buffer.put(int2(timestamp));
        buffer.put(int1(timestamp));
        buffer.put(int0(timestamp));
        buffer.put(int2(randomValue1));
        buffer.put(int1(randomValue1));
        buffer.put(int0(randomValue1));
        if (!this.noRandomValue2) {
            buffer.put(short1(randomValue2));
            buffer.put(short0(randomValue2));
        }
        buffer.put(int2(counter));
        buffer.put(int1(counter));
        buffer.put(int0(counter));
    }

    /**
     * Converts this instance into a 24-byte hexadecimal string representation.
     *
     * @return a string representation of the ObjectId in hexadecimal format
     */
    public String toHexString() {
        char[] chars = null;
        if (this.noRandomValue2) {
            chars = new char[OBJECT_ID_LENGTH_NOPID * 2];
        }
        else {
            chars = new char[OBJECT_ID_LENGTH * 2];
        }

        int i = 0;
        for (byte b : toByteArray()) {
            chars[i++] = HEX_CHARS[b >> 4 & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    @Override
    public String toString() {
        return toHexString();
    }

    static {
        SecureRandom secureRandom = new SecureRandom();
        RANDOM_VALUE1 = secureRandom.nextInt(0x01000000);
        RANDOM_VALUE2 = (short) secureRandom.nextInt(0x00008000);
    }

    private static byte int3(final int x) {
        return (byte) (x >> 24);
    }

    private static byte int2(final int x) {
        return (byte) (x >> 16);
    }

    private static byte int1(final int x) {
        return (byte) (x >> 8);
    }

    private static byte int0(final int x) {
        return (byte) (x);
    }

    private static byte short1(final short x) {
        return (byte) (x >> 8);
    }

    private static byte short0(final short x) {
        return (byte) (x);
    }

}
