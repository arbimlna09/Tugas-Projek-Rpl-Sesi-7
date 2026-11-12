package com.praktikum.rmi.web;

import com.praktikum.rmi.model.Mahasiswa;
import com.praktikum.rmi.service.MahasiswaService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Web Servlet Controller (Client RMI & Web Interface)
 * Memenuhi Spesifikasi:
 * - Soal 3: Integrasi dengan Aplikasi Web (Tampil data, form input, cari NIM, tabel HTML, panggil method RMI)
 * - HOTS: CRUD lengkap (Tambah, Cari, Update, Hapus) dan Autentikasi Login Administrator.
 */
@WebServlet(name = "MahasiswaServlet", urlPatterns = {"/mahasiswa"})
public class MahasiswaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String RMI_HOST = "localhost";
    public static final int RMI_PORT = 1099;
    public static final String SERVICE_NAME = "MahasiswaService";

    private MahasiswaService getRemoteService() throws Exception {
        Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
        return (MahasiswaService) registry.lookup(SERVICE_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String nim = request.getParameter("nim");
        String nama = request.getParameter("nama");
        String editNim = request.getParameter("editNim");
        String msg = request.getParameter("msg");
        String msgType = request.getParameter("msgType");

        boolean statusKoneksi = false;
        String errorMessage = null;
        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();
        int jumlahMahasiswa = 0;
        Mahasiswa editMahasiswa = null;

        try {
            MahasiswaService service = getRemoteService();
            jumlahMahasiswa = service.getJumlahMahasiswa();
            statusKoneksi = true;

            // Jika dalam mode edit data (HOTS Update)
            if (editNim != null && !editNim.trim().isEmpty()) {
                editMahasiswa = service.getMahasiswaByNim(editNim.trim());
            }

            // Filter data
            if (nim != null && !nim.trim().isEmpty()) {
                Mahasiswa m = service.getMahasiswaByNim(nim.trim());
                if (m != null) {
                    daftarMahasiswa.add(m);
                }
            } else if (nama != null && !nama.trim().isEmpty()) {
                daftarMahasiswa = service.cariMahasiswaByNama(nama.trim());
            } else {
                daftarMahasiswa = service.getAllMahasiswa();
            }

        } catch (Exception e) {
            statusKoneksi = false;
            errorMessage = "KONEKSI GAGAL: Tidak dapat terhubung ke RMI Server (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")";
            daftarMahasiswa = Collections.emptyList();
            jumlahMahasiswa = 0;
            System.err.println("[MahasiswaServlet] Gagal memanggil RMI Server: " + e.getMessage());
        }

        // Simpan data ke request scope
        request.setAttribute("statusKoneksi", statusKoneksi);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("daftarMahasiswa", daftarMahasiswa);
        request.setAttribute("jumlahMahasiswa", jumlahMahasiswa);
        request.setAttribute("editMahasiswa", editMahasiswa);
        request.setAttribute("queryNim", nim != null ? nim : "");
        request.setAttribute("queryNama", nama != null ? nama : "");
        request.setAttribute("feedbackMsg", msg);
        request.setAttribute("feedbackType", msgType);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        String feedbackMsg = "";
        String feedbackType = "info";

        try {
            MahasiswaService service = getRemoteService();

            if ("login".equalsIgnoreCase(action)) {
                // HOTS: Fitur Login Administrator
                String user = request.getParameter("username");
                String pass = request.getParameter("password");
                if (service.loginAdmin(user, pass)) {
                    session.setAttribute("adminLogged", true);
                    session.setAttribute("adminUser", user);
                    feedbackMsg = "Login Administrator berhasil! Anda memiliki akses penuh.";
                    feedbackType = "success";
                } else {
                    feedbackMsg = "Login Gagal! Username atau password administrator salah.";
                    feedbackType = "danger";
                }
            } else if ("logout".equalsIgnoreCase(action)) {
                session.removeAttribute("adminLogged");
                session.removeAttribute("adminUser");
                feedbackMsg = "Anda telah logout.";
                feedbackType = "info";
            } else if ("tambah".equalsIgnoreCase(action)) {
                String nim = request.getParameter("nim");
                String nama = request.getParameter("nama");
                String jurusan = request.getParameter("jurusan");

                if (nim == null || nim.trim().isEmpty() ||
                    nama == null || nama.trim().isEmpty() ||
                    jurusan == null || jurusan.trim().isEmpty()) {
                    feedbackMsg = "Semua field wajib diisi!";
                    feedbackType = "danger";
                } else {
                    Mahasiswa mhsBaru = new Mahasiswa(nim.trim(), nama.trim(), jurusan.trim());
                    boolean success = service.tambahMahasiswa(mhsBaru);
                    if (success) {
                        feedbackMsg = "Mahasiswa NIM " + nim + " berhasil ditambahkan ke RMI Server!";
                        feedbackType = "success";
                    } else {
                        feedbackMsg = "Gagal! NIM " + nim + " sudah terdaftar.";
                        feedbackType = "danger";
                    }
                }
            } else if ("update".equalsIgnoreCase(action)) {
                // HOTS: Fitur Update Mahasiswa
                String nim = request.getParameter("nim");
                String nama = request.getParameter("nama");
                String jurusan = request.getParameter("jurusan");

                Mahasiswa mhsUpdate = new Mahasiswa(nim.trim(), nama.trim(), jurusan.trim());
                boolean success = service.updateMahasiswa(mhsUpdate);
                if (success) {
                    feedbackMsg = "Data Mahasiswa NIM " + nim + " berhasil diperbarui!";
                    feedbackType = "success";
                } else {
                    feedbackMsg = "Gagal memperbarui data Mahasiswa NIM " + nim;
                    feedbackType = "danger";
                }
            } else if ("hapus".equalsIgnoreCase(action)) {
                String nim = request.getParameter("nim");
                if (nim != null && !nim.trim().isEmpty()) {
                    boolean success = service.hapusMahasiswa(nim.trim());
                    if (success) {
                        feedbackMsg = "Mahasiswa dengan NIM " + nim + " berhasil dihapus!";
                        feedbackType = "success";
                    } else {
                        feedbackMsg = "Gagal menghapus! Data tidak ditemukan.";
                        feedbackType = "danger";
                    }
                }
            }

        } catch (Exception e) {
            feedbackMsg = "Gagal menghubungi RMI Server: " + e.getMessage();
            feedbackType = "danger";
        }

        String redirectUrl = request.getContextPath() + "/mahasiswa?msg=" +
                URLEncoder.encode(feedbackMsg, StandardCharsets.UTF_8.toString()) +
                "&msgType=" + URLEncoder.encode(feedbackType, StandardCharsets.UTF_8.toString());

        response.sendRedirect(redirectUrl);
    }
}
