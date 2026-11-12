package com.praktikum.rmi.service;

import com.praktikum.rmi.model.Mahasiswa;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface Remote RMI: MahasiswaService
 * Sesuai Spesifikasi Tugas Praktikum Pemrograman Jaringan Sesi 7.
 * Mendeklarasikan kontrak method remote yang dapat dipanggil dari jarak jauh oleh Client.
 */
public interface MahasiswaService extends Remote {

    /**
     * Mengambil seluruh data mahasiswa dari server.
     */
    List<Mahasiswa> getAllMahasiswa() throws RemoteException;

    /**
     * Mengambil satu data mahasiswa berdasarkan NIM.
     */
    Mahasiswa getMahasiswaByNim(String nim) throws RemoteException;

    /**
     * Menambahkan data mahasiswa baru ke penyimpanan server.
     */
    boolean tambahMahasiswa(Mahasiswa mhs) throws RemoteException;

    /**
     * Fitur HOTS: Memperbarui data mahasiswa (Update) untuk melengkapi CRUD.
     */
    boolean updateMahasiswa(Mahasiswa mhs) throws RemoteException;

    /**
     * Menghapus data mahasiswa dari server berdasarkan NIM.
     */
    boolean hapusMahasiswa(String nim) throws RemoteException;

    /**
     * Menghitung total jumlah mahasiswa yang tersimpan di server.
     */
    int getJumlahMahasiswa() throws RemoteException;

    /**
     * Fitur HOTS: Mencari data mahasiswa berdasarkan nama (substring match).
     */
    List<Mahasiswa> cariMahasiswaByNama(String nama) throws RemoteException;

    /**
     * Fitur HOTS: Validasi login administrator.
     */
    boolean loginAdmin(String username, String password) throws RemoteException;
}
