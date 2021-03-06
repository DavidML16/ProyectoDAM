CREATE DATABASE db_proyecto;

USE db_proyecto;


CREATE TABLE profesor (
    id_profesor INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    numero INTEGER(5),
    nombre VARCHAR(150) NOT NULL,
    abreviacion VARCHAR(5) NOT NULL,
    minhorasdia INTEGER(3),
    maxhorasdia INTEGER(3),
    departamento VARCHAR(50) NOT NULL
);


CREATE TABLE credencial (
    id_credencial INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(255) NOT NULL,
	passwd_hash VARCHAR(255) NOT NULL,
    profesor INTEGER(11),
    rol VARCHAR(50) NOT NULL,
    CONSTRAINT credencial_prof_fk FOREIGN KEY (profesor) REFERENCES profesor(id_profesor) ON DELETE CASCADE
);


CREATE TABLE aula (
    id_aula INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL
);


CREATE TABLE curso (
    id_curso INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    nivel VARCHAR(10) NOT NULL,
    nombre VARCHAR(150) NOT NULL
);


CREATE TABLE asignatura (
    id_asignatura INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    numero INTEGER(5) NOT NULL,
    abreviacion VARCHAR(150) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
	color VARCHAR(9)
);


CREATE TABLE curso_asignatura (
    curso INTEGER(11),
    asignatura INTEGER(11),
    CONSTRAINT c_s_pk PRIMARY KEY (curso, asignatura),
    CONSTRAINT c_fk FOREIGN KEY (curso) REFERENCES curso(id_curso) ON DELETE CASCADE,
    CONSTRAINT a_fk FOREIGN KEY (asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE
);


CREATE TABLE numero_dia (
	id_dia INTEGER(11) PRIMARY KEY,
    dia VARCHAR(150) NOT NULL
);


CREATE TABLE numero_hora (
	id_hora INTEGER(11) PRIMARY KEY,
    horas VARCHAR(150) NOT NULL
);


CREATE TABLE franja_horaria (
	id_franja INTEGER(11) PRIMARY KEY,
    dia INTEGER(11) NOT NULL,
    hora INTEGER(11) NOT NULL,
    CONSTRAINT franja_dia FOREIGN KEY (dia) REFERENCES numero_dia(id_dia) ON DELETE CASCADE,
    CONSTRAINT franja_hora FOREIGN KEY (hora) REFERENCES numero_hora(id_hora) ON DELETE CASCADE
);


CREATE TABLE grupo (
    id_grupo INTEGER(11) PRIMARY KEY AUTO_INCREMENT,
    curso INTEGER(11) NOT NULL,
    letra VARCHAR(10) NOT NULL,
	CONSTRAINT grupo_fk FOREIGN KEY (curso) REFERENCES curso(id_curso)
);


CREATE TABLE imparte (
	uuid VARCHAR(36) PRIMARY KEY,
	profesor INTEGER(11) NOT NULL,
	asignatura INTEGER(11) NOT NULL,
	grupo INTEGER(11) NULL,
	aula INTEGER(11) NULL,
	franja INTEGER(11) NOT NULL,
	CONSTRAINT imparte_profesor_fk FOREIGN KEY (profesor) REFERENCES profesor(id_profesor),
	CONSTRAINT imparte_asignatura_fk FOREIGN KEY (asignatura) REFERENCES asignatura(id_asignatura),
	CONSTRAINT imparte_grupo_fk FOREIGN KEY (grupo) REFERENCES grupo(id_grupo),
	CONSTRAINT imparte_aula_fk FOREIGN KEY (aula) REFERENCES aula(id_aula),
	CONSTRAINT imparte_franja_fk FOREIGN KEY (franja) REFERENCES franja_horaria(id_franja)
);

