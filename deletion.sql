 gym database


-- Drop tables with foreign key dependencies first
DROP TABLE IF EXISTS client_work_class;
DROP TABLE IF EXISTS trainer_class_work_class;
DROP TABLE IF EXISTS trainer_class_specialty;
DROP TABLE IF EXISTS pertrainer_specialty;
DROP TABLE IF EXISTS work_class_schedules;
DROP TABLE IF EXISTS Inscription;

-- Drop remaining tables
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS per_trainer;
DROP TABLE IF EXISTS trainers_class;
DROP TABLE IF EXISTS detail_user;
DROP TABLE IF EXISTS detail_per_trainer;
DROP TABLE IF EXISTS detail_class_trainer;
DROP TABLE IF EXISTS membership;
DROP TABLE IF EXISTS specialty;
DROP TABLE IF EXISTS work_class;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS schedulesGym;
DROP TABLE IF EXISTS promotion;
DROP TABLE IF EXISTS equipament;
DROP TABLE IF EXISTS pool;

-- Confirm all tables are deleted
SHOW TABLES;
