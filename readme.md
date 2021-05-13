# ComposeCap桌面端协议抓包软件(仿WireShark)


----

## 支持的功能
- 协议抓包
- 报文构造
- 流量统计
- 网络计费

## 使用到的库有

- UI库([Jetpack Compose Desktop](https://github.com/JetBrains/compose-jb))
- 抓包库 ([Pcap4j](https://github.com/kaitoy/pcap4j)) 底层使用libpcap

---

### 协议抓包
协议抓包支持以Hex形式查看报文的信息，以及解析支持的报文的主要字段

**支持的协议类型**
- Ethernet
- ARP
- IPv4
- ICMP
- TCP
- UDP

![capture](https://github.com/Icyrockton/ComposeCap/blob/master/img/capture.gif)

协议抓包还支持BPF(Berkeley Packet Filter)的过滤条件
例如如下的条件语句
- tcp port 80 (只抓TCP包，并且端口为80)
- icmp (只抓ICMP类型的包)
- src port 443 (只抓源端口为443的包)

更多的BPF过滤语句请查看 [IBM Berkeley packet filters](https://www.ibm.com/docs/en/qsip/7.4?topic=queries-berkeley-packet-filters)

![filter](https://github.com/Icyrockton/ComposeCap/blob/master/img/filter.gif)

---
### 报文构造
报文构造只支持下列协议
- Ethernet
- IPv4
- UDP
- TCP
且构造的字节数少于60字节数，会自动进行补**0x00**填充

![packetBuild](https://github.com/Icyrockton/ComposeCap/blob/master/img/packetBuild.gif)

---
### 流量统计
流量统计功能部分为统计1S内的流量数，部分为统计总流量数

流量统计的思路为判断以太网报文中的目的MAC地址是否为**本机MAC地址**，若是则为下载流量，否则为上传流量
- 上传速度(KB/S)
- 下载速度(KB/S)
- 每秒上传包数量(Packet/S)
- 每秒下载包数量(Packet/S)
- ARP包数量
- ICMP包数量
- TCP包数量
- UDP包数量
- DNS包数量
- 总下载流量
- 总上传流量
- 总流量

![traffic](https://github.com/Icyrockton/ComposeCap/blob/master/img/speed.gif)

---
### 流量计费
流量计费仅计算价格，输入每MB的费用，计算需要支付的金额😄

![fee](https://github.com/Icyrockton/ComposeCap/blob/master/img/fee.png)
