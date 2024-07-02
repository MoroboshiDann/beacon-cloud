package org.moroboshidan.smsgateway.netty4.entity;

import org.moroboshidan.smsgateway.netty4.util.Command;
import org.moroboshidan.smsgateway.netty4.util.MsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 与CMPP建立连接需要的信息
 */
public class CmppConnect extends CmppMessageHeader {

    private String serviceId;

    private String pwd;

    public CmppConnect(String serviceId, String pwd) {
        super(Command.CMPP_CONNECT, Command.CMPP2_VERSION);
        this.serviceId = serviceId;
        this.pwd = pwd;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer(4 + 4 + 4 + 6 + 16 + 1 + 4);

        //Total_Length
        buf.writeInt(4 + 4 + 4 + 6 + 16 + 1 + 4);
        //Command_Id
        buf.writeInt(Command.CMPP_CONNECT);
        //Sequence_Id
        buf.writeInt(MsgUtil.getSequence());
        //sourceAddr
        buf.writeBytes(MsgUtil.getLenBytes(serviceId, 6));
        //authenticatorSource
        buf.writeBytes(MsgUtil.getAuthenticatorSource(serviceId, pwd));
        //version
        buf.writeByte(1);
        //timestamp
        buf.writeInt(Integer.parseInt(MsgUtil.getTimestamp()));

        return buf.array();
    }
}
