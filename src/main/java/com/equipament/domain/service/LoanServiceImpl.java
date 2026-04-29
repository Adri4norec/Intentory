package com.equipament.domain.service;

import com.equipament.application.dto.EquipmentLoanResponse;
import com.equipament.application.dto.LoanListResponse;
import com.equipament.application.dto.LoanRequest;
import com.equipament.application.dto.UpdateLoanStatusRequest;
import com.equipament.domain.enums.EquipmentUsage;
import com.equipament.domain.enums.LoanStatus;
import com.equipament.domain.model.Equipament;
import com.equipament.domain.model.Loan;
import com.equipament.domain.model.Status;
import com.equipament.infraestructure.EquipamentRepository;
import com.equipament.infraestructure.LoanRepository;
import com.identity.domain.UserEntity;
import com.identity.infrastructure.UserRepository;
import com.user.application.dto.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EquipamentRepository equipamentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public LoanListResponse getById(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com o ID: " + loanId));

        Equipament e = loan.getEquipament();

        return new LoanListResponse(
                loan.getId(),
                String.valueOf(e.getTopo()),
                e.getCategoria(),
                e.getName(),
                e.getDescription(),
                loan.getStatus().name(),
                loan.getLoanDate(),
                loan.getReturnDate()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanListResponse> listEquipmentsForLoanScreen() {
        List<Equipament> equipaments = equipamentRepository.findAll().stream()
                .filter(e -> e.getUsageType() != null &&
                        EquipmentUsage.COLABORADOR.equals(e.getUsageType()) &&
                        e.getStatus() != null &&
                        "DISPONIVEL".equalsIgnoreCase(e.getStatus().getStatus()))
                .toList();

        List<Loan> activeLoans = loanRepository.findAll().stream()
                .filter(l -> l.getStatus() != LoanStatus.DEVOLVIDO &&
                        l.getStatus() != LoanStatus.CANCELADO)
                .toList();

        Map<UUID, Loan> loanMap = activeLoans.stream()
                .collect(Collectors.toMap(
                        l -> l.getEquipament().getId(),
                        l -> l,
                        (existing, replacement) -> existing
                ));

        return equipaments.stream().map(e -> {
            Loan loan = loanMap.get(e.getId());

            String statusExibicao;
            if (loan != null) {
                statusExibicao = loan.getStatus().name();
            } else {
                statusExibicao = e.getStatus().getStatus();
            }

            return new LoanListResponse(
                    loan != null ? loan.getId() : e.getId(),
                    String.valueOf(e.getTopo()),
                    e.getCategoria(),
                    e.getName(),
                    e.getDescription(),
                    statusExibicao,
                    loan != null ? loan.getLoanDate() : null,
                    loan != null ? loan.getReturnDate() : null
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanListResponse> advancedSearch(String nome, String categoria, String tombo,
                                                 String caracteristicas, String status,
                                                 Pageable pageable) {
        return loanRepository.searchAdvanced(nome, categoria, tombo, caracteristicas, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentLoanResponse findByCodeToLoan(String topo) {
        Equipament equipament = equipamentRepository.findByTopo(topo)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com o código: " + topo));

        validateEquipmentEligibility(equipament);

        return new EquipmentLoanResponse(
                equipament.getId(),
                equipament.getName(),
                equipament.getDescription(),
                equipament.getStatus().getStatus(),
                equipament.getCategoria(),
                equipament.getTopo()
        );
    }

    @Override
    @Transactional
    public Loan saveLoanPreparation(LoanRequest request) {
        Equipament equipament = equipamentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        validateEquipmentEligibility(equipament);

        UserEntity collaborator = userRepository.findById(request.colaboradorId())
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        UserEntity tecnico = null;

        Loan loan = Loan.createPreparation(
                equipament,
                tecnico,
                collaborator,
                request.loanDate(),
                request.returnDate(),
                request.helpdeskTicket(),
                request.observation()
        );

        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void updateLoanStatus(UUID loanId, UpdateLoanStatusRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        LoanStatus newStatus = LoanStatus.valueOf(request.newStatus().toUpperCase());

        loan.changeStatus(newStatus);

        atualizarStatusEquipamentoRelacionado(loan, newStatus);

        loanRepository.save(loan);
    }

    private void atualizarStatusEquipamentoRelacionado(Loan loan, LoanStatus novoStatus) {
        String statusTexto = switch (novoStatus) {
            case PREPARACAO -> "EM_PREPARACAO";
            case PRONTO, AGUARDANDO_DOCUMENTACAO, AGUARDANDO_ASSINATURA -> "RESERVADO";
            case AGUARDANDO_RETIRADA, EM_USO -> "EM_EMPRESTIMO";
            case DEVOLVIDO, CANCELADO -> "DISPONIVEL";
            case EMPRESTIMO_FINALIZADO -> "FINALIZADO";
            default -> "OCUPADO";
        };

        loan.getEquipament().getStatus().updateStatus(
                loan.getEquipament().getStatus().getStatusType(),
                statusTexto
        );
    }

    @Override
    @Transactional
    public void registerReturn(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado para o ID: " + loanId));

        if (loan.getStatus() == LoanStatus.DEVOLVIDO) {
            throw new RuntimeException("Este empréstimo já consta como devolvido.");
        }

        loan.changeStatus(LoanStatus.DEVOLVIDO);

        loanRepository.save(loan);

        Equipament equipament = loan.getEquipament();
        Status currentStatus = equipament.getStatus();
        if (currentStatus != null) {
            currentStatus.updateStatus(currentStatus.getStatusType(), "DISPONIVEL");
        }

        equipamentRepository.save(equipament);
    }

    private void validateEquipmentEligibility(Equipament equipament) {
        if (!"COLABORADOR".equalsIgnoreCase(equipament.getUsageType().name())) {
            throw new RuntimeException("Equipamento não destinado a colaboradores.");
        }

        String currentStatus = equipament.getStatus().getStatus();

        if ("EM_MANUTENCAO".equals(currentStatus) || "INDISPONIVEL".equals(currentStatus)) {
            throw new RuntimeException("Equipamento em estado inválido para empréstimo: " + currentStatus);
        }

        if (!"DISPONIVEL".equals(currentStatus)) {
            throw new RuntimeException("Equipamento já está em uso ou preparação.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsersByName(String nome) {
        return userRepository.findByNameContainingIgnoreCase(nome)
                .stream()
                .map(u -> new UserSearchResponse(u.getId(), u.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Set<String> uploadDocuments(UUID loanId, List<MultipartFile> files) {

        Loan loan = findLoanOrThrow(loanId);
        validateFiles(files);

        Path directoryPath = createDirectory(loanId);

        for (MultipartFile file : files) {
            if (isInvalidFile(file)) continue;

            validatePdf(file);

            String fileName = generateFileName(file);
            Path filePath = directoryPath.resolve(fileName);

            saveFile(file, filePath);

            String relativeUrl = buildRelativeUrl(loanId, fileName);
            loan.addDocumentUrl(relativeUrl);
        }

        loanRepository.save(loan);
        return loan.getDocumentUrls();
    }

    private Loan findLoanOrThrow(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com o ID: " + loanId));
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Nenhum arquivo enviado.");
        }
    }

    private boolean isInvalidFile(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private Path createDirectory(UUID loanId) {
        try {
            Path path = Paths.get(uploadDir, "loans", loanId.toString(), "documents");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            return path;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretório", e);
        }
    }

    private void validatePdf(MultipartFile file) {
        String originalName = file.getOriginalFilename();

        boolean isPdfByName = originalName != null && originalName.toLowerCase().endsWith(".pdf");
        boolean isPdfByType = file.getContentType() != null &&
                file.getContentType().equalsIgnoreCase("application/pdf");

        if (!isPdfByName && !isPdfByType) {
            throw new RuntimeException("Apenas arquivos PDF são permitidos. Arquivo inválido: " + originalName);
        }
    }

    private String generateFileName(MultipartFile file) {

        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "document.pdf";

        String safeName = sanitizeFileName(originalName);

        if (!safeName.toLowerCase().endsWith(".pdf")) {
            safeName += ".pdf";
        }

        return UUID.randomUUID() + "_" + safeName;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replace("\\", "_").replace("/", "_");
    }

    private void saveFile(MultipartFile file, Path path) {
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    private String buildRelativeUrl(UUID loanId, String fileName) {
        return "loans/" + loanId + "/documents/" + fileName;
    }

//    @Override
//    @Transactional
//    public Set<String> uploadDocuments(UUID loanId, List<MultipartFile> files) {
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com o ID: " + loanId));
//
//        if (files == null || files.isEmpty()) {
//            throw new RuntimeException("Nenhum arquivo enviado.");
//        }
//
//        try {
//            Path directoryPath = Paths.get(uploadDir, "loans", loanId.toString(), "documents");
//            if (!Files.exists(directoryPath)) {
//                Files.createDirectories(directoryPath);
//            }
//
//            for (MultipartFile file : files) {
//                if (file == null || file.isEmpty()) {
//                    continue;
//                }
//
//                String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
//                boolean isPdfByName = originalName.toLowerCase().endsWith(".pdf");
//                boolean isPdfByType = file.getContentType() != null && file.getContentType().equalsIgnoreCase("application/pdf");
//                if (!isPdfByName && !isPdfByType) {
//                    throw new RuntimeException("Apenas arquivos PDF são permitidos. Arquivo inválido: " + originalName);
//                }
//
//                String safeOriginalName = originalName.replace("\\", "_").replace("/", "_");
//                if (!safeOriginalName.toLowerCase().endsWith(".pdf")) {
//                    safeOriginalName = safeOriginalName + ".pdf";
//                }
//
//                String fileName = UUID.randomUUID() + "_" + safeOriginalName;
//                Path filePath = directoryPath.resolve(fileName);
//                Files.write(filePath, file.getBytes());
//
//                String relativeUrl = "loans/" + loanId + "/documents/" + fileName;
//                loan.addDocumentUrl(relativeUrl);
//            }
//
//            loanRepository.save(loan);
//            return loan.getDocumentUrls();
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao processar arquivos PDF", e);
//        }
//    }
}