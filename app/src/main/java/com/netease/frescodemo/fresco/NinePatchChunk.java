package com.netease.frescodemo.fresco;

import android.graphics.Rect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NinePatchChunk {

    public final Rect mPaddings = new Rect();

    public int mDivX[];
    public int mDivY[];
    public int mColor[];

    private static void readIntArray(final int[] data, final ByteBuffer buffer) {
        for (int i = 0, n = data.length; i < n; ++i)
            data[i] = buffer.getInt();
    }

    private static void checkDivCount(final int length) {
        if (length == 0 || (length & 0x01) != 0)
            throw new RuntimeException("invalid nine-patch: " + length);
    }

    public static NinePatchChunk deserialize(final byte[] data) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());

        if (byteBuffer.get() == 0) return null; // is not serialized

        final NinePatchChunk chunk = new NinePatchChunk();
        chunk.mDivX = new int[byteBuffer.get()];
        chunk.mDivY = new int[byteBuffer.get()];
        chunk.mColor = new int[byteBuffer.get()];

        checkDivCount(chunk.mDivX.length);
        checkDivCount(chunk.mDivY.length);

        // skip 8 bytes
        byteBuffer.getInt();
        byteBuffer.getInt();

        chunk.mPaddings.left = byteBuffer.getInt();
        chunk.mPaddings.right = byteBuffer.getInt();
        chunk.mPaddings.top = byteBuffer.getInt();
        chunk.mPaddings.bottom = byteBuffer.getInt();

        // skip 4 bytes
        byteBuffer.getInt();

        readIntArray(chunk.mDivX, byteBuffer);
        readIntArray(chunk.mDivY, byteBuffer);
        readIntArray(chunk.mColor, byteBuffer);

        return chunk;
    }
}