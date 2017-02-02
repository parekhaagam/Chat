package example.android.com.chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Honey on 01-Feb-17.
 */

class PacketMessage {
    public String message;
}