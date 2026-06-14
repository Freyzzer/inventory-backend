package com.inventario.inventory_backend.company;

import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.user.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        Company company = new Company(null, request.name().trim(), request.plan(), true, null,
                request.email().trim().toLowerCase(), passwordEncoder.encode(request.password()), Role.OWNER);
        company = companyRepository.save(company);
        return toResponse(company);
    }

    @Transactional(readOnly = true)
    public Company findActiveById(Long id) {
        return companyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id " + id));
    }

    static CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getPlan(),
                company.getActive(),
                company.getCreatedAt()
        );
    }
}
