CREATE DATABASE db_proyecto;

USE db_proyecto;

CREATE TABLE profesor (
    id_prof INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    numero INTEGER(5),
    nombre VARCHAR(150) NOT NULL,
    abrev VARCHAR(5),
    minhdia INTEGER(3),
    maxhdia INTEGER(3),
    depart VARCHAR(50)
);

CREATE TABLE credencial (
    id_cred INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
	passwd_hash VARCHAR(255),
    profesor INTEGER(11),
    rol VARCHAR(50),
    CONSTRAINT credencial_prof_fk FOREIGN KEY (profesor) REFERENCES profesor(id_prof)
);