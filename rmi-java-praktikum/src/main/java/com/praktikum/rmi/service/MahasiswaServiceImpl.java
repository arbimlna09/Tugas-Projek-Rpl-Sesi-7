package com.praktikum.rmi.service;

import com.praktikum.rmi.model.Mahasiswa;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementasi Remote Service Mahasiswa
 * Meng-extend UnicastRemoteObject dan mengimplementasikan MahasiswaService.
 *
 * Menggunakan CopyOnWriteArrayList<Mahasiswa> untuk thread-safety pada akses konkuren
 * dari banyak client web / thread secara bersamaan (HOTS).
 */
public class MahasiswaServiceImpl extends UnicastRemoteObject implements MahasiswaService {
    private static final long serialVersionUID = 1L;

    // In-memory data store yang thread-safe
    private final List<Mahasiswa> listMahasiswa;

    public MahasiswaServiceImpl() throws RemoteException {
        super();
        this.listMahasiswa = new CopyOnWriteArrayList<>();

        // Data inisialisasi awal
        this.listMahasiswa.add(new Mahasiswa("220001", "Budi Santoso", "Teknik Informatika"));
        this.listMahasiswa.add(new Mahasiswa("220002", "Siti Aminah", "Sistem Informasi"));
        this.listMahasiswa.add(new Mahasiswa("220003", "Ahmad Fauzi", "Teknik Komputer"));
        this.listMahasiswa.add(new Mahasiswa("220004", "Rina Marlina", "Rekayasa Perangkat Lunak"));

        System.out.println("[MahasiswaServiceImpl] Remote object berhasil di-export dengan " + listMahasiswa.size() + " data awal.");
    }

    @Override
    public List<Mahasiswa> getAllMahasiswa() throws RemoteException {
        System.out.println("[RMI CALL] getAllMahasiswa() -> total: " + listMahasiswa.size());
        return new ArrayList<>(this.listMahasiswa);
    }

    @Override
    public Mahasiswa getMahasiswaByNim(String nim) throws RemoteException {
        System.out.println("[RMI CALL] getMahasiswaByNim('" + nim + "')");
        if (nim == null) return null;

        for (Mahasiswa m : listMahasiswa) {
            if (m.getNim().equalsIgnoreCase(nim.trim())) {
                return m;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean tambahMahasiswa(Mahasiswa mhs) throws RemoteException {
        System.out.println("[RMI CALL] tambahMahasiswa(" + mhs + ")");
        if (mhs == null || mhs.getNim() == null || mhs.getNim().trim().isEmpty()) {
            return false;
        }

        // Validasi keunikan NIM
        for (Mahasiswa m : listMahasiswa) {
            if (m.getNim().equalsIgnoreCase(mhs.getNim().trim())) {
                System.out.println("[RMI REJECT] NIM " + mhs.getNim() + " sudah ada.");
                return false;
            }
        }

        Mahasiswa baru = new Mahasiswa(
                mhs.getNim().trim(),
                mhs.getNama().trim(),
                mhs.getJurusan().trim()
        );
        boolean added = listMahasiswa.add(baru);
        System.out.println("[RMI SUCCESS] Data berhasil ditambahkan. Total sekarang: " + listMahasiswa.size());
        return added;
    }

    @Override
    public synchronized boolean updateMahasiswa(Mahasiswa mhs) throws RemoteException {
        System.out.println("[RMI CALL] updateMahasiswa(" + mhs + ")");
        if (mhs == null || mhs.getNim() == null) return false;

        for (int i = 0; i < listMahasiswa.size(); i++) {
            Mahasiswa m = listMahasiswa.get(i);
            if (m.getNim().equalsIgnoreCase(mhs.getNim().trim())) {
                m.setNama(mhs.getNama().trim());
                m.setJurusan(mhs.getJurusan().trim());
                System.out.println("[RMI SUCCESS] Data NIM " + mhs.getNim() + " berhasil diupdate.");
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean hapusMahasiswa(String nim) throws RemoteException {
        System.out.println("[RMI CALL] hapusMahasiswa('" + nim + "')");
        if (nim == null) return false;

        boolean removed = listMahasiswa.removeIf(m -> m.getNim().equalsIgnoreCase(nim.trim()));
        if (removed) {
            System.out.println("[RMI SUCCESS] NIM " + nim + " berhasil dihapus. Sisa: " + listMahasiswa.size());
        } else {
            System.out.println("[RMI NOT FOUND] NIM " + nim + " tidak ditemukan.");
        }
        return removed;
    }

    @Override
    public int getJumlahMahasiswa() throws RemoteException {
        System.out.println("[RMI CALL] getJumlahMahasiswa() -> " + listMahasiswa.size());
        return listMahasiswa.size();
    }

    @Override
    public List<Mahasiswa> cariMahasiswaByNama(String nama) throws RemoteException {
        System.out.println("[RMI CALL] cariMahasiswaByNama('" + nama + "')");
        List<Mahasiswa> hasil = new ArrayList<>();

        if (nama == null || nama.trim().isEmpty()) {
            return new ArrayList<>(this.listMahasiswa);
        }

        String keyword = nama.trim().toLowerCase();
        for (Mahasiswa m : listMahasiswa) {
            if (m.getNama() != null && m.getNama().toLowerCase().contains(keyword)) {
                hasil.add(m);
            }
        }
        return hasil;
    }

    @Override
    public boolean loginAdmin(String username, String password) throws RemoteException {
        System.out.println("[RMI CALL] loginAdmin('" + username + "')");
        // Kredensial default admin
        return "admin".equals(username) && "admin123".equals(password);
    }
}
