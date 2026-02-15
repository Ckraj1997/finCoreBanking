package mca.fincorebanking.entity;

public enum Role {
    CUSTOMER,           // Retail: View balances, transfers
    CORPORATE,          // Corporate: Bulk payments, treasury tools
    TELLER,             // Branch Staff: Cash deposits/withdrawals for customers
    RELATIONSHIP_MGR,   // RM: Advisory, client portfolio view
    COMPLIANCE,         // Compliance: Audit trails, fraud monitoring (Read-Only)
    ADMIN,              // System Admin: User management, config
    AUDITOR,            // External/Internal: Read-only logs & reports
    MANAGER,            // Bank Manager: High-value approvals, branch ops
    SUPER_ADMIN         // Central IT: Infrastructure, disaster recovery
}