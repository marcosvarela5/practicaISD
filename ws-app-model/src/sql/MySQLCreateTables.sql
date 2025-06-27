-- ----------------------------------------------------------------------------
-- Model
-- -----------------------------------------------------------------------------
DROP TABLE Attendance;
DROP TABLE Event;
-- --------------------------------Event----------------------------------------
CREATE TABLE Event
(
    eventId         BIGINT                           NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) COLLATE latin1_bin  NOT NULL,
    description     VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    celebrationDate DATETIME                         NOT NULL,
    duration        INT                              NOT NULL,
    creationDate    DATETIME                         NOT NULL,
    activo          BIT                              NOT NULL,
    going           INT                              NOT NULL,
    notGoing        INT                              NOT NULL,
    CONSTRAINT EventPK PRIMARY KEY (eventId)
) ENGINE = InnoDB;
-- Attendance ------------------------------------
CREATE TABLE Attendance
(
    attendanceId BIGINT      NOT NULL AUTO_INCREMENT,
    eventId      BIGINT      NOT NULL,
    email        VARCHAR(50) NOT NULL,
    answer       BIT(1)      NOT NULL,
    checkInTime  DATETIME    NOT NULL,
    CONSTRAINT AttendancePK primary key (attendanceId),
    CONSTRAINT EventFK foreign key (eventId) REFERENCES Event (eventId) ON DELETE CASCADE
) ENGINE = InnoDB;


