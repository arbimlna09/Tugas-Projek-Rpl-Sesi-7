package com.praktikum.rmi.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model Data Mahasiswa
 * Mengimplementasikan java.io.Serializable agar objek dapat di-marshal (diserialisasi)
 * dan ditransmisikan melalui jaringan pada arsitektur Java RMI.
 */
public class Mahasiswa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nim;
    private String nama;
    private String jurusan;

    // Default Constructor (dibutuhkan untuk serialisasi Java Beans)
    public Mahasiswa() {
    }

    // Parameterized Constructor
    public Mahasiswa(String nim, String nama, String jurusan) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
    }

    // Getter & Setter
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mahasiswa mahasiswa = (Mahasiswa) o;
        return Objects.equals(nim, mahasiswa.nim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nim);
    }

    @Override
    public String toString() {
        return "Mahasiswa{" +
                "nim='" + nim + '\'' +
                ", nama='" + nama + '\'' +
                ", jurusan='" + jurusan + '\'' +
                '}';
    }
}
