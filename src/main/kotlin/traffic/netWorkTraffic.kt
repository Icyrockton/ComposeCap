package traffic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import data.viewModel
import org.jetbrains.skija.Paint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
/**
 *@Author : Icyrockton
 *@Date: 2021/5/12 18:37
 **/

@Composable
fun NetWorkTraffic() {
    val uploadTraffic by viewModel.pcap.uploadTraffic.collectAsState()
    val downloadTraffic by viewModel.pcap.downloadTraffic.collectAsState()
    val uploadPacket by viewModel.pcap.uploadPacket.collectAsState()
    val downloadPacket  by viewModel.pcap.downloadPacket.collectAsState()
    val icmpPacket  by viewModel.pcap.icmpPacket.collectAsState()
    val arpPacket  by viewModel.pcap.arpPacket.collectAsState()
    val tcpPacket  by viewModel.pcap.tcpPacket.collectAsState()
    val udpPacket  by viewModel.pcap.udpPacket.collectAsState()
    val dnsPacket  by viewModel.pcap.dnsPacket.collectAsState()
    val totalDownloadTraffic  by viewModel.pcap.totalDownloadTraffic.collectAsState()
    val totalUploadTraffic  by viewModel.pcap.totalUploadTraffic.collectAsState()
    val totalTraffic  by viewModel.pcap.totalTraffic.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(end = 20.dp,top = 40.dp,bottom = 40.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                TrafficCircularProgressBar(
                    title = "上传速度",
                    currentValue = uploadTraffic / 1024,
                    maxValue = 1024,
                    unit = "KB/S",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFBD1B1),
                    color = Color(0xFFF58634)
                )
                TrafficCircularProgressBar(
                    title = "下载速度",
                    currentValue = downloadTraffic / 1024,
                    maxValue = 1024,
                    unit = "KB/S",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFFECAD),
                    color = Color(0xFFFFCC29)
                )
                TrafficCircularProgressBar(
                    title = "每秒上传包数量",
                    currentValue = uploadPacket,
                    maxValue = 200,
                    unit = "Packet/S",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFCFE2A5),
                    color = Color(0xFF81B214)
                )
                TrafficCircularProgressBar(
                    title = "每秒下载包数量",
                    currentValue = downloadPacket,
                    maxValue = 200,
                    unit = "Packet/S",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFAAC6C1),
                    color = Color(0xFF206A5D)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                TrafficCircularProgressBar(
                    title = "ARP包数量",
                    currentValue = arpPacket,
                    maxValue = 100,
                    unit = "个",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFD0C1E8),
                    color = Color(0xFF845EC2)
                )
                TrafficCircularProgressBar(
                    title = "ICMP包数量",
                    currentValue = icmpPacket,
                    maxValue = 100,
                    unit = "个",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFFEAC2),
                    color = Color(0xFFFFC75F)
                )
                TrafficCircularProgressBar(
                    title = "TCP包数量",
                    currentValue = tcpPacket,
                    maxValue = 1000,
                    unit = "个",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFDFCC9),
                    color = Color(0xFFF9F871)
                )
                TrafficCircularProgressBar(
                    title = "UDP包数量",
                    currentValue = udpPacket,
                    maxValue = 1000,
                    unit = "个",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFFC1CB),
                    color = Color(0xFFFF5E78)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                TrafficCircularProgressBar(
                    title = "DNS包数量",
                    currentValue = dnsPacket,
                    maxValue = 100,
                    unit = "个",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFBC1B9),
                    color = Color(0xFFF55C47)
                )
                TrafficCircularProgressBar(
                    title = "总下载流量",
                    currentValue = totalDownloadTraffic / 1024 / 1024,
                    maxValue = 100,
                    unit = "MB",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFD2E3EF),
                    color = Color(0xFF8AB6D6)
                )
                TrafficCircularProgressBar(
                    title = "总上传流量",
                    currentValue = totalUploadTraffic/ 1024 / 1024,
                    maxValue = 1000,
                    unit = "MB",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFC6B0E8),
                    color = Color(0xFF6930C3)
                )
                TrafficCircularProgressBar(
                    title = "总流量",
                    currentValue = totalTraffic / 1024 / 1024,
                    maxValue = 1000,
                    unit = "MB",
                    modifier = Modifier.weight(0.25f),
                    backGroundColor = Color(0xFFFFBD9E),
                    color = Color(0xFFFF5200)
                )
            }
        }
    }
}


@Composable
fun TrafficCircularProgressBar(
    title: String,
    currentValue: Long, //最小值
    maxValue: Long,//最大值
    unit: String, //单位
    color: Color,
    backGroundColor: Color,
    modifier: Modifier = Modifier
) {

    val animatedValue = remember { Animatable(currentValue.toFloat()) }
    LaunchedEffect(currentValue) { //动画效果
        animatedValue.animateTo(currentValue.toFloat(), animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }
    Card(
        modifier = Modifier.fillMaxHeight().then(modifier).padding(10.dp, vertical = 0.dp),
        shape = RoundedCornerShape(20.dp),
        contentColor = Color.Gray,
        elevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().weight(0.2f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.Black, modifier = Modifier.padding(top = 10.dp))
            }
            Box(modifier = Modifier.fillMaxWidth().weight(0.8f), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val circleRadius = this.size.minDimension / 2.5f
                    val arcWidth = 10.0f //外圈的宽度
                    val arcTopLeft = this.center - Offset(circleRadius, circleRadius) + Offset(
                        arcWidth / 2.0f,
                        arcWidth / 2.0f
                    )//弧形的左上角

                    //背景
                    drawCircle(color = backGroundColor, radius = circleRadius)

                    //圆弧
                    drawArc(
                        color = color,
                        -90f,
                        360f * (animatedValue.value / maxValue),
                        false,
                        style = Stroke(width = arcWidth, cap = StrokeCap.Round),
                        topLeft = arcTopLeft,
                        size = Size((circleRadius - arcWidth / 2.0f) * 2.0f, (circleRadius - arcWidth / 2.0f) * 2.0f)
                    )


                    drawIntoCanvas {
                        with(it.nativeCanvas) {
                            val paint = Paint()
                        }
                    }
                }
                Text(text = "${currentValue} ${unit}", color = Color.Black)
            }
        }

    }
}
