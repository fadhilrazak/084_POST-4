package com.dhil.post_4_fadhil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhil.post_4_fadhil.databinding.ActivityDetailBinding
import kotlin.text.insert

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var executor: AppExecutor
    private lateinit var dao: WargaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = AppExecutor()
        dao = DatabaseWarga.getDatabase(this).wargaDao()

        // Ambil ID dari Intent
        val wargaId = intent.getIntExtra("warga_id", -1)
        if (wargaId == -1) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        executor.diskIO.execute {
            val warga = dao.getAllWarga().value?.find { it.id == wargaId }

            runOnUiThread {
                if (warga != null) {
                    // Isi form dengan data warga
                    binding.etNama.setText(warga.nama)
                    binding.etJenis.setText(warga.jenisKelamin)
                    binding.etharga.setText(warga.statusPernikahan)
                }
            }

            binding.btnUpdate.setOnClickListener {
                val updated = Warga(
                    id = wargaId,
                    nama = binding.etNama.text.toString(),
                    nik = warga?.nik ?: "",
                    kabupaten = warga?.kabupaten ?: "",
                    kecamatan = warga?.kecamatan ?: "",
                    desa = warga?.desa ?: "",
                    rt = warga?.rt ?: "",
                    rw = warga?.rw ?: "",
                    jenisKelamin = binding.etJenis.text.toString(),
                    statusPernikahan = binding.etharga.text.toString()
                )

                executor.diskIO.execute {
                    dao.insert(updated) // atau update() jika kamu tambahkan ke DAO
                }
                runOnUiThread {
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnDelete.setOnClickListener {
                executor.diskIO.execute {
                    warga?.let { dao.deleteAll() } // ganti delete(warga) kalau kamu tambahkan di DAO
                }
                runOnUiThread {
                    Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}