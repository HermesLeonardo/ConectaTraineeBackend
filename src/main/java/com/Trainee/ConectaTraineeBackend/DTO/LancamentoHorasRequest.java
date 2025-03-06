package com.Trainee.ConectaTraineeBackend.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LancamentoHorasRequest {

    private Long idAtividade;
    private String descricao;
    private String dataInicio;  // Formato esperado: "dd/MM"
    private String horaInicio;   // Formato esperado: "HH:mm"
    private String horaFim;      // Formato esperado: "HH:mm"


    public Long getIdAtividade() {
        return idAtividade;
    }


    public void setIdAtividade(Long idAtividade) {
        this.idAtividade = idAtividade;
    }


    public String getDescricao() {
        return descricao;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }




}
