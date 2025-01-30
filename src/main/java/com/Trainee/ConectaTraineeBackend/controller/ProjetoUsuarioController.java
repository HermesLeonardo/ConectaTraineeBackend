package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import com.Trainee.ConectaTraineeBackend.service.ProjetoUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos-usuarios")
public class ProjetoUsuarioController {

    @Autowired
    private ProjetoUsuarioService projetoUsuarioService;

    @PostMapping("/{idProjeto}/{idUsuario}")
    public ResponseEntity<ProjetoUsuario> adicionarUsuarioAoProjeto(@PathVariable Long idProjeto, @PathVariable Long idUsuario) {
        return ResponseEntity.ok(projetoUsuarioService.adicionarUsuarioAoProjeto(idProjeto, idUsuario));
    }


    //retorna todos os projetos associados ao usuário com o idUsuario especificado
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ProjetoUsuario>> listarProjetosPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(projetoUsuarioService.listarProjetosPorUsuario(idUsuario));
    }


    //retorna todos os usuários associados ao projeto com o idProjeto especificado
    @GetMapping("/projeto/{idProjeto}")
    public ResponseEntity<List<ProjetoUsuario>> listarUsuariosPorProjeto(@PathVariable Long idProjeto) {
        return ResponseEntity.ok(projetoUsuarioService.listarUsuariosPorProjeto(idProjeto));
    }
}
