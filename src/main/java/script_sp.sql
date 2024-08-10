



-- first store procedure
CREATE DEFINER=`root`@`localhost` PROCEDURE `converter`()
BEGIN
SET SQL_SAFE_UPDATES = 0;

    -- Verifica se ci sono record da elaborare per la prima parte
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 1
          AND objective = 0
          AND percentage_save_money = 100
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN
        -- 0 Gestione caso quando il 100% del valore deve essere risparmiato
        INSERT INTO totale_risparmiato (data, euro_risparmiati, registro_eventi_id)
        SELECT
            data,
            value AS euro_risparmiati,
            registro_eventi_id
        FROM
            registro_eventi
        WHERE
            saved_money = 1
            AND objective = 0
            AND percentage_save_money = 100
            AND triggered = 0
			AND type_event ='ENTRATA';


        -- Aggiorna il campo triggered per gli eventi che sono stati inseriti in totale_risparmiato
        UPDATE registro_eventi
        SET triggered = TRUE
        WHERE saved_money = 1
          AND objective = 0
          AND percentage_save_money = 100
          AND triggered = 0
          AND type_event ='ENTRATA';

    END IF;

    -- Verifica se ci sono record da elaborare per la seconda parte
    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event ='ENTRATA'
    ) THEN
        -- 2 Inserimento solo nella tabella gestione_spese
        INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
        SELECT
            value,
            registro_eventi_id,
            NULL
        FROM
            registro_eventi
        WHERE
            saved_money = 0
            AND objective = 0
            AND percentage_save_money = 0
            AND triggered = 0
            AND type_event ='ENTRATA';

        -- Aggiorna il campo triggered per gli eventi che sono stati inseriti in gestione_spese
        UPDATE registro_eventi
        SET triggered = TRUE
        WHERE saved_money = 0
          AND objective = 0
          AND percentage_save_money = 0
          AND triggered = 0
          AND type_event ='ENTRATA';
    END IF;



    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE 	saved_money = 1
        AND objective = 0
        AND percentage_save_money < 100
		AND triggered = 0
        AND type_event ='ENTRATA'
    ) THEN

	INSERT INTO totale_risparmiato (data, euro_risparmiati, registro_eventi_id)
    SELECT
		data,
        value - (value * (percentage_save_money / 100)) AS euro_risparmiati,
        registro_eventi_id

    FROM
        registro_eventi
    WHERE
        saved_money = 1
        AND objective = 0
        AND percentage_save_money < 100
        AND triggered = 0
        AND type_event ='ENTRATA';


	INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
    SELECT
        r.value * (r.percentage_save_money / 100) AS euro_spendibili,
        r.registro_eventi_id,
        tr.euro_risparmiati_id
    FROM
        registro_eventi r
    INNER JOIN totale_risparmiato tr
        ON r.registro_eventi_id = tr.registro_eventi_id
    WHERE
        r.saved_money = 1
        AND r.objective = 0
        AND r.percentage_save_money < 100;

   UPDATE registro_eventi
    SET triggered = TRUE
    WHERE saved_money = 1
      AND objective = 0
      AND percentage_save_money < 100
      AND type_event ='ENTRATA';
    END IF;


    -- case spesa

    IF EXISTS (
        SELECT 1
        FROM registro_eventi
        WHERE   type_event ='SPESA'
    ) THEN
    INSERT INTO gestione_spese (euro_disponibili, registro_eventi_id, euro_risparmiati_id)
    SELECT
        r.value * (-1) AS spesa,
        r.registro_eventi_id,
        null
    FROM
        registro_eventi r
    WHERE
     type_event ='SPESA';

	UPDATE registro_eventi
    SET triggered = TRUE
    WHERE type_event ='SPESA';
    END IF;
SET SQL_SAFE_UPDATES = 1;

END







-- second store procedure
CREATE DEFINER=`root`@`localhost` PROCEDURE `Finanza_disponibile`(out totale double)
BEGIN
     DECLARE total_amount DOUBLE;
     SELECT SUM(euro_disponibili) INTO total_amount
    FROM gestione_spese;
     SET totale = total_amount;
END


