package org.moroboshidan.smsgateway.netty4;


import org.moroboshidan.common.util.CMPP2ResultUtil;
import org.moroboshidan.smsgateway.netty4.entity.CmppDeliver;
import org.moroboshidan.smsgateway.netty4.entity.CmppSubmitResp;
import org.moroboshidan.smsgateway.netty4.util.MsgUtil;
import org.moroboshidan.smsgateway.runnable.DeliverRunnable;
import org.moroboshidan.smsgateway.runnable.SubmitRepoRunnable;
import org.moroboshidan.smsgateway.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 主要业务 handler,运营商响应信息
 */
@Slf4j
public class CMPPHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object msg) throws Exception {

        if (msg instanceof CmppSubmitResp) {
            CmppSubmitResp resp = (CmppSubmitResp) msg;
            log.info("-------------接收到短信提交应答-------------");
            log.info("----自增id：" + resp.getSequenceId());
            log.info("----状态：" + resp.getResult());
            log.info("----第一次响应：" + resp.getMsgId());
            // 将封装好的任务扔到线程池中，执行即可
            ThreadPoolExecutor cmppSubmitPool = (ThreadPoolExecutor) SpringUtil.getBeanByName("cmppSubmitPool");
            cmppSubmitPool.execute(new SubmitRepoRunnable(resp));
        }

        if (msg instanceof CmppDeliver) {
            CmppDeliver resp = (CmppDeliver) msg;
            // 是否为状态报告 0：非状态报告1：状态报告
            if (resp.getRegistered_Delivery() == 1) {
                // 如果是状态报告的话
                log.info("-------------状态报告---------------");
                log.info("----第二次响应：" + resp.getMsg_Id_DELIVRD());
                log.info("----手机号：" + resp.getDest_terminal_Id());
                log.info("----状态：" + resp.getStat());
                // 提前获取一手~~~
                ThreadPoolExecutor cmppDeliverPool = (ThreadPoolExecutor) SpringUtil.getBeanByName("cmppDeliverPool");
                cmppDeliverPool.execute(new DeliverRunnable(resp.getMsg_Id_DELIVRD(), resp.getStat()));
            } else {
                // 用户回复会打印在这里
                log.info("" + MsgUtil.bytesToLong(resp.getMsg_Id()));
                log.info(resp.getSrc_terminal_Id());
                log.info(resp.getMsg_Content());
            }
        }
    }

}
