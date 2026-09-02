package com.labanta.servidorlocal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void enviarEmailBoasVindas(String emailDestino, String nomeUtilizador){
        try {
            // Criar um email simples(texto limpo)
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(emailDestino);
            mensagem.setTo(emailDestino);
            mensagem.setSubject("bem vindo ao Marketplace!");
            mensagem.setText("Olá " + nomeUtilizador + "!\n\n" +
                    "A tua conta foi criada com sucesso. Já podes fazer login e explorar os nossos serviços. \n\n" +
                    "Com os melhores cumprimentos. \nEquipa do MarketPlace");

            mailSender.send(mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar email de boas-vindas para {}: {}", emailDestino, e.getMessage());
        }
    }

    //funÇÃO
    public void enviarOrcamentoPorEmail(String emailDestino, String nomeServico,
                                        double precoConvertido, String moeda) {

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setSubject("orcamento do servico no maketplace!");
        String corpo = String.format(
                "Ola!\n\nAqui tens o orcamento solicitado para o servico:\n\n" +
                        "Servico: %s\n " +
                        "Preco Final: %.2f %s\n\n" +
                        "Este valor foi calculado com a taxa de cambio em tempo real.\n" +
                        "Obrigado por usares o nosso Marketplace!",
                nomeServico, precoConvertido, moeda
        );
        mensagem.setText(corpo);
        mailSender.send(mensagem);
    }

    public void enviarAlertaSeguranca(String emailDestino, String cidade, String pais){
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(emailDestino);
        mensagem.setTo(emailDestino);
        mensagem.setSubject("Aviso de Seguranca no seu maketplace!");
        String corpo = String.format(
                "Ola!\n\nAviso de Segurança:\n\n" +
                       "Detetámos uma nova atividade na tua conta do Marketplace a partir de  %s, %s.\n\n"  +
                        "Se não foste tu, altera a tua password imediatamente!",
                cidade, pais
        );
        mensagem.setText(corpo);
        mailSender.send(mensagem);

    }
}