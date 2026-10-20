package com.chekhoff.print;

import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

// Sends raw ESC/POS bytes straight to the printer on port 9100 (no browser restrictions).
@CapacitorPlugin(name = "RawPrint")
public class RawPrintPlugin extends Plugin {

    @PluginMethod
    public void send(PluginCall call) {
        final String ip = call.getString("ip");
        final int port = call.getInt("port", 9100);
        final String data = call.getString("data");
        if (ip == null || ip.isEmpty() || data == null) {
            call.reject("ข้อมูลไม่ครบ");
            return;
        }
        new Thread(() -> {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, port), 5000);
                socket.setSoTimeout(15000);
                OutputStream out = socket.getOutputStream();
                out.write(Base64.decode(data, Base64.DEFAULT));
                out.flush();
                call.resolve(new JSObject());
            } catch (Exception e) {
                call.reject("ติดต่อเครื่องพิมพ์ไม่ได้ ตรวจ IP และ Wi-Fi (" + e.getMessage() + ")");
            }
        }).start();
    }
}
