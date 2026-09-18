package com.asan.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.produk.entity.Produk;
import com.asan.produk.repository.ProdukRepository;

@Service
public class ProdukService {
  @Autowired
  private ProdukRepository produkRepository;

  public Produk getProdukById(Long id) {
    return produkRepository.findById(id).orElse(null);
  }

  public List<Produk> getAllProduk() {
    return produkRepository.findAll();
  }

  public Produk saveProduk(Produk produk) {
    return produkRepository.save(produk);
  }

  public void deleteProduk(Long id) {
    produkRepository.deleteById(id);
  }

  public Produk updateProduk(Long id, Produk produk) {
    Produk existingProduk = produkRepository.findById(id).orElse(null);
    if (existingProduk != null) {
      existingProduk.setNama(produk.getNama());
      existingProduk.setHarga(produk.getHarga());
      existingProduk.setDeskripsi(produk.getDeskripsi());
      existingProduk.setIdJenis(produk.getIdJenis());
      return produkRepository.save(existingProduk);
    }
    return null;
  }

  public List<Produk> getAllBarangByIdJenis(Long idjenis) {
    return produkRepository.getAllBarangByIdJenis(idjenis);
  }

}
