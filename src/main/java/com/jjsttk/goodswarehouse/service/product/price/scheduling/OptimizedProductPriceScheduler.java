package com.jjsttk.goodswarehouse.service.product.price.scheduling;

import com.jjsttk.goodswarehouse.configuration.property.service.scheduling.SchedulingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Scheduler that periodically increases the prices of all products in the `product` table by a configured percentage.
 * <p>
 * This component is intended for production (`@Profile("prod")`)
 * and is activated only if the following properties are enabled:
 * <ul>
 *     <li>app.scheduling.enabled = true</li>
 *     <li>app.scheduling.optimization.enabled = true</li>
 * </ul>
 * </p>
 * <p>
 * Key features:
 * <ul>
 *     <li>Uses {@link JdbcTemplate} for direct SQL queries.</li>
 *     <li>Can lock the `product` table in <strong>ACCESS EXCLUSIVE</strong> mode during execution,
 *     preventing any parallel operations (SELECT, INSERT, UPDATE, DELETE).</li>
 *     <li>Writes all updated rows to a file while processing,
 *     without loading all data into memory.</li>
 *     <p>Default file name is {@code default: scheduling-result.log}.</p>
 * </ul>
 * </p>
 */
@Slf4j
@Profile("prod")
@ConditionalOnExpression("${app.scheduling.enabled:false} && ${app.scheduling.optimization.enabled:false}")
@Component
@RequiredArgsConstructor
public class OptimizedProductPriceScheduler implements ProductPriceScheduler {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Scheduling properties, filled from application.yml to property class.
     */
    private final SchedulingProperties schedulingProperties;

    /**
     * Main scheduler method.
     * <p>
     * Runs according to the schedule specified by {@code app.scheduling.period}.
     * It performs the following actions:
     * <ol>
     *     <li>If {@code app.scheduling.optimization.use-exclusive-lock: true})
     *     then locks the `product` table in ACCESS EXCLUSIVE mode.</li>
     *     <li>Updates the prices of all products using SQL {@code UPDATE ... RETURNING *}.</li>
     *     <li>Writes all updated rows in file with default name {@code default: scheduling-result.log}.</li>
     *     <p>File name can be changed here {@code app.scheduling.optimization.output-file-name=your.name}</p>
     * </ol>
     * </p>
     *
     * @throws RuntimeException if there is an error writing to the file or executing the SQL.
     */
    @Override
    @Scheduled(fixedDelayString = "${app.scheduling.period}")
    @Transactional
    public void runTask() {
        var useExclusiveLock = schedulingProperties.getOptimization().isUseExclusiveLock();
        log.info("OptimizedProductPriceScheduler running with parameter exclusiveLock: \"{}\".", useExclusiveLock);

        var increaseBy = schedulingProperties.getPriceIncreasePercentage();
        var calculatedMultiplier = BigDecimal.ONE.add(
                increaseBy.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        var returnUpdatedRowsSql = "UPDATE product SET price = price * ? RETURNING *";

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(schedulingProperties.getOptimization().getOutputFileName(), false)
        )) {
            if (useExclusiveLock) {
                jdbcTemplate.execute("LOCK TABLE product IN ACCESS EXCLUSIVE MODE");
            }

            jdbcTemplate.query(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(returnUpdatedRowsSql);
                        ps.setBigDecimal(1, calculatedMultiplier);
                        return ps;
                    },
                    rs -> {
                        writeRow(writer, rs);
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a single row from the ResultSet to the output file.
     *
     * @param writer {@link BufferedWriter} used to write to the file
     * @param rs     {@link ResultSet} containing the current product row
     * @throws RuntimeException if there is an error reading from the ResultSet or writing to the file
     */
    private void writeRow(BufferedWriter writer, ResultSet rs) {
        try {
            writer.write(String.format(
                    "id=%s, name=%s, article=%s, category=%s, price=%s, quantity=%s, description=%s%n",
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("article"),
                    rs.getString("category"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("quantity"),
                    rs.getString("description")
            ));
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
