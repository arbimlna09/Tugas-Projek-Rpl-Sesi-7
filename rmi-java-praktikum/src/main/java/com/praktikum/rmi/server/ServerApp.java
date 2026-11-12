package com.praktikum.rmi.server;

import com.praktikum.rmi.service.MahasiswaService;
import com.praktikum.rmi.service.MahasiswaServiceImpl;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApp {
    private static final int PORT = 1099;
    private static final String SERVICE_NAME = "MahasiswaService";

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("🚀 [RMI SERVER] Memulai Server Remote Method Invocation...");
        System.out.println("==========================================================");

        try {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", localIp);

            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println("✔ Berhasil membuat RMI Registry baru pada Port: " + PORT);

            MahasiswaService service = new MahasiswaServiceImpl();
            registry.rebind(SERVICE_NAME, service);
            System.out.println("✔ Service didaftarkan ke RMI Registry dengan nama: '" + SERVICE_NAME + "'");
            System.out.println("----------------------------------------------------------");
            System.out.println("📡 Status Server : ONLINE (Siap melayani remote invocations)");
            System.out.println("📍 Port Registry : " + PORT);
            System.out.println("📌 Local URL     : rmi://localhost:" + PORT + "/" + SERVICE_NAME);
            System.out.println("📌 LAN URL       : rmi://" + localIp + ":" + PORT + "/" + SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("❌ Gagal memulai RMI Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}