-- V1__Create_tables.sql

CREATE TABLE bank_accounts (
                               id BIGSERIAL PRIMARY KEY,
                               account_type VARCHAR(20) NOT NULL,
                               account_name VARCHAR(100) NOT NULL,
                               account_number VARCHAR(50) UNIQUE NOT NULL,
                               current_balance DECIMAL(19,2) NOT NULL CHECK (current_balance >= 0),
                               bank_name VARCHAR(100) NOT NULL,
                               branch_name VARCHAR(100) NOT NULL
);

CREATE TABLE employees (
                           employee_id VARCHAR(4) PRIMARY KEY CHECK (employee_id ~ '^[0-9]{4}$'),
                           name VARCHAR(100) NOT NULL,
                           grade VARCHAR(20) NOT NULL,
                           address TEXT NOT NULL,
                           mobile_number VARCHAR(20) NOT NULL,
                           bank_account_id BIGINT UNIQUE REFERENCES bank_accounts(id) ON DELETE CASCADE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE company (
                         id BIGSERIAL PRIMARY KEY,
                         company_code VARCHAR(20) UNIQUE NOT NULL DEFAULT 'MAIN',
                         bank_account_id BIGINT REFERENCES bank_accounts(id),
                         base_salary_lowest_grade DECIMAL(19,2)
);

CREATE TABLE salary_transactions (
                                     id BIGSERIAL PRIMARY KEY,
                                     employee_id VARCHAR(4) REFERENCES employees(employee_id),
                                     basic_salary DECIMAL(19,2) NOT NULL,
                                     house_rent DECIMAL(19,2) NOT NULL,
                                     medical_allowance DECIMAL(19,2) NOT NULL,
                                     total_salary DECIMAL(19,2) NOT NULL,
                                     status VARCHAR(30) NOT NULL,
                                     transaction_date TIMESTAMP NOT NULL,
                                     transaction_reference VARCHAR(100),
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_employee_grade ON employees(grade);
CREATE INDEX idx_transaction_date ON salary_transactions(transaction_date);
CREATE INDEX idx_transaction_status ON salary_transactions(status);
