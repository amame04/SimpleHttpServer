package io.github.amame04.android.simplehttpserver

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.net.SocketException

class IPAddress {
    fun getIPAddressList() : List<String> {
        var addressList = mutableListOf<String>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            if (interfaces == null) {
                addressList.add("0")
                return addressList
            }
            while(interfaces.hasMoreElements()){
                val network = interfaces.nextElement()
                val addresses = network.inetAddresses

                while(addresses.hasMoreElements()){
                    val address = addresses.nextElement()
                    addressList.add(address.hostAddress)
                }
            }
        } catch (e : SocketException) {
            e.printStackTrace()
        }
        return addressList
    }

    fun getIPAddress(context: Context) : String {
        val manager : WifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = manager.connectionInfo.ipAddress
        return String.format(
            "%d.%d.%d.%d",
            ipAddress shr 0 and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }
}