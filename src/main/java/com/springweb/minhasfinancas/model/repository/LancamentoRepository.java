package com.springweb.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springweb.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository <Lancamento, Long>{

}
