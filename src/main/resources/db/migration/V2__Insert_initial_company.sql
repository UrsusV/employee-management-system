-- Insert company bank account
INSERT INTO bank_accounts (account_type, account_name, account_number, current_balance, bank_name, branch_name)
VALUES ('CURRENT', 'Company Main Account', 'COMP-001-2024', 1000000.00, 'Corporate Bank', 'Main Branch');

-- Insert company record
INSERT INTO company (company_code, bank_account_id, base_salary_lowest_grade)
SELECT 'MAIN', id, 30000.00 FROM bank_accounts WHERE account_number = 'COMP-001-2024';