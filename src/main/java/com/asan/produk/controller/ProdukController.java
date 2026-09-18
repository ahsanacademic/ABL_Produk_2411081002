package com.asan.produk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asan.produk.entity.JenisProduk;
import com.asan.produk.entity.Produk;
import com.asan.produk.service.JenisProdukService;
import com.asan.produk.service.ProdukService;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

  @Autowired
  private ProdukService produkService;

  @Autowired
  private JenisProdukService jenisProdukService;

  @GetMapping
  public List<Produk> getAllProduk(@RequestParam(value = "idjenis", required = false) Long idjenis) {
    if (idjenis != null) {
      return produkService.getAllBarangByIdJenis(idjenis);
    }
    return produkService.getAllProduk();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Produk> getProdukById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(produkService.getProdukById(id));
  }

  @PostMapping
  public ResponseEntity<Produk> createProduk(@RequestBody Produk produk) {
    return ResponseEntity.ok(produkService.saveProduk(produk));
  }

  // jenis produk

  @GetMapping("/jenis")
  public List<JenisProduk> getAllJenisProduk() {
    return jenisProdukService.getAllJenisProduk();
  }

  @GetMapping("/jenis/{id}")
  public ResponseEntity<JenisProduk> getJenisProdukById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(jenisProdukService.getJenisProdukById(id));
  }

  @PostMapping("/jenis")
  public ResponseEntity<JenisProduk> createJenisProduk(@RequestBody JenisProduk jenisProduk) {
    return ResponseEntity.ok(jenisProdukService.saveJenisProduk(jenisProduk));
  }
}
