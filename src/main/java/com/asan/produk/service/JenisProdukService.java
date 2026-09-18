package com.asan.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.produk.entity.JenisProduk;
import com.asan.produk.entity.Produk;
import com.asan.produk.repository.JenisProdukRepository;
import com.asan.produk.repository.ProdukRepository;

@Service
public class JenisProdukService {
  @Autowired
  private JenisProdukRepository jenisProdukRepository;

  public JenisProduk getJenisProdukById(Long id) {
    return jenisProdukRepository.findById(id).orElse(null);
  }

  public List<JenisProduk> getAllJenisProduk() {
    return jenisProdukRepository.findAll();
  }

  public JenisProduk saveJenisProduk(JenisProduk jenisProduk) {
    return jenisProdukRepository.save(jenisProduk);
  }

  public void deleteJenisProduk(Long id) {
    jenisProdukRepository.deleteById(id);
  }

  public JenisProduk updateJenisProduk(Long id, JenisProduk jenisProduk) {
    JenisProduk existingJenisProduk = jenisProdukRepository.findById(id).orElse(null);
    if (existingJenisProduk != null) {
      existingJenisProduk.setJenis(jenisProduk.getJenis());
      return jenisProdukRepository.save(existingJenisProduk);
    }
    return null;
  }
}