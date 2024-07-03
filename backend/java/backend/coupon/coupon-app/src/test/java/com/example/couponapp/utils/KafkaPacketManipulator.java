package com.example.couponapp.utils;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class KafkaPacketManipulator {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPacketManipulator.class);
    private static final String FILTER_EXPRESSION = "tcp port 9092";
    private final PcapNetworkInterface nif;
    private final AtomicInteger packetCount = new AtomicInteger(0);

    public KafkaPacketManipulator(String deviceName) throws PcapNativeException {
        this.nif = Pcaps.getDevByName(deviceName);
        if (this.nif == null) {
            throw new IllegalArgumentException("No such device: " + deviceName);
        }
    }

    public void startLogging() throws PcapNativeException, NotOpenException, InterruptedException {
        PcapHandle handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
        handle.setFilter(FILTER_EXPRESSION, BpfProgram.BpfCompileMode.OPTIMIZE);

        PacketListener listener = packet -> {
            int currentCount = packetCount.incrementAndGet();
            logger.info("Packet #{}: {}", currentCount, packetToString(packet));
        };

        logger.info("Starting packet capture on interface: {}", nif.getName());
        handle.loop(-1, listener);
    }

    private String packetToString(Packet packet) {
        StringBuilder sb = new StringBuilder();
        sb.append("Packet Type: ").append(packet.getClass().getSimpleName()).append("\n");

        if (packet instanceof IpPacket) {
            IpPacket ipPacket = (IpPacket) packet;
            sb.append("Source IP: ").append(ipPacket.getHeader().getSrcAddr()).append("\n");
            sb.append("Destination IP: ").append(ipPacket.getHeader().getDstAddr()).append("\n");
        }

        if (packet instanceof TcpPacket) {
            TcpPacket tcpPacket = (TcpPacket) packet;
            sb.append("Source Port: ").append(tcpPacket.getHeader().getSrcPort()).append("\n");
            sb.append("Destination Port: ").append(tcpPacket.getHeader().getDstPort()).append("\n");
            sb.append("Sequence Number: ").append(tcpPacket.getHeader().getSequenceNumber()).append("\n");
            sb.append("Ack Number: ").append(tcpPacket.getHeader().getAcknowledgmentNumber()).append("\n");
            sb.append("Flags: ").append(tcpPacket.getHeader().getSyn() ? "SYN " : "")
                .append(tcpPacket.getHeader().getAck() ? "ACK " : "")
                .append(tcpPacket.getHeader().getFin() ? "FIN " : "")
                .append(tcpPacket.getHeader().getRst() ? "RST " : "").append("\n");
        }

        byte[] rawData = packet.getRawData();
        sb.append("Payload (Hex): ").append(bytesToHex(rawData)).append("\n");
        sb.append("Payload (ASCII): ").append(bytesToAscii(rawData)).append("\n");

        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private static String bytesToAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (b >= 32 && b < 127) {
                sb.append((char) b);
            } else {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            KafkaPacketManipulator logger = new KafkaPacketManipulator("eth0"); // 네트워크 인터페이스 이름을 적절히 변경하세요
            logger.startLogging();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//Exception in thread "main" java.lang.UnsatisfiedLinkError: /Users/jumonglee/Library/Caches/JNA/temp/jna9000090153184678412.tmp: dlopen(/Users/jumonglee/Library/Caches/JNA/temp/jna9000090153184678412.tmp, 0x0001): tried: '/Users/jumonglee/Library/Caches/JNA/temp/jna9000090153184678412.tmp' (fat file, but missing compatible architecture (have 'i386,x86_64', need 'arm64')), '/System/Volumes/Preboot/Cryptexes/OS/Users/jumonglee/Library/Caches/JNA/temp/jna9000090153184678412.tmp' (no such file), '/Users/jumonglee/Library/Caches/JNA/temp/jna9000090153184678412.tmp' (fat file, but missing compatible architecture (have 'i386,x86_64', need 'arm64'))
//	at java.base/jdk.internal.loader.NativeLibraries.load(Native Method)
//	at java.base/jdk.internal.loader.NativeLibraries$NativeLibraryImpl.open(NativeLibraries.java:388)
//	at java.base/jdk.internal.loader.NativeLibraries.loadLibrary(NativeLibraries.java:232)
//	at java.base/jdk.internal.loader.NativeLibraries.loadLibrary(NativeLibraries.java:174)
//	at java.base/java.lang.ClassLoader.loadLibrary(ClassLoader.java:2394)
//	at java.base/java.lang.Runtime.load0(Runtime.java:755)
//	at java.base/java.lang.System.load(System.java:1953)
//	at com.sun.jna.Native.loadNativeDispatchLibraryFromClasspath(Native.java:1018)
//	at com.sun.jna.Native.loadNativeDispatchLibrary(Native.java:988)
//	at com.sun.jna.Native.<clinit>(Native.java:195)
//	at com.sun.jna.ptr.PointerByReference.<init>(PointerByReference.java:40)
//	at com.sun.jna.ptr.PointerByReference.<init>(PointerByReference.java:36)
//	at org.pcap4j.core.Pcaps.findAllDevs(Pcaps.java:51)
//	at org.pcap4j.core.Pcaps.getDevByName(Pcaps.java:125)
//	at com.example.couponapp.utils.KafkaPacketManipulator.<init>(KafkaPacketManipulator.java:17)
//	at com.example.couponapp.utils.KafkaPacketManipulator.main(KafkaPacketManipulator.java:87)


}
