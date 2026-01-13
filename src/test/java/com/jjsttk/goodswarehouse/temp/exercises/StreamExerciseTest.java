package com.jjsttk.goodswarehouse.temp.exercises;

import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.testutil.OrderTestDataFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
class StreamExerciseTest {

    @Test
    void ordersToMapUsingStreamTest() {
        var ord1 = Ord.builder()
                .id(UUID.randomUUID())
                .prods(List.of(
                        new Prod("1"),
                        new Prod("2")
                ))
                .build();

        var ord2 = Ord.builder()
                .id(UUID.randomUUID())
                .prods(List.of(
                        new Prod("2"),
                        new Prod("3")
                ))
                .build();

        var ord3 = Ord.builder()
                .id(UUID.randomUUID())
                .prods(List.of(
                        new Prod("1"),
                        new Prod("2")
                ))
                .build();

        var orders = List.of(ord1, ord2, ord3);

        log.info("GroupByOption res: {}.", firstOptionUsingStreamWithGroupBy(orders));
        log.info("ToMapOption res: {}.", secondOptionUsingStreamWithToMap(orders));

    }

    @Test
    void ordersToMapUsingStreamTestBenchmarkXD() {
        var ordsForWarmUp = generateOrdsByLength(10000, 2);
        firstOptionUsingStreamWithGroupBy(ordsForWarmUp);
        secondOptionUsingStreamWithToMap(ordsForWarmUp);


        var ordsForBench = generateOrdsByLength(50000, 2);
        var sw = StopWatch.createStarted();
        firstOptionUsingStreamWithGroupBy(ordsForBench);
        sw.stop();
        log.info("GroupBy time: {} ms", sw.getTime(TimeUnit.MILLISECONDS));
        sw.reset();
        sw.start();
        secondOptionUsingStreamWithToMap(ordsForBench);
        sw.stop();
        log.info("ToMap time: {} ms", sw.getTime(TimeUnit.MILLISECONDS));
    }

    @Test
    void usingTeeingExample() {
        var orderEntity = OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                5,
                OrderStatus.CREATED,
                BigDecimal.TEN,
                true
        );
        var orderServiceResponse = OrderTestDataFactory.getBaseOrderServiceResponse(orderEntity);

        var orderControllerResponse =
                orderServiceResponse.products().stream()
                        .collect(Collectors.teeing(
                                // (mappedProds)
                                Collectors.mapping(it -> GetOrderProductResponse.builder()
                                                .productId(it.productId())
                                                .name(it.name())
                                                .quantity(it.quantity())
                                                .price(it.price())
                                                .build(),
                                        Collectors.toList()),

                                // (totalOrderPrice)
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        it -> it.price().multiply(it.quantity()),
                                        BigDecimal::add
                                ),

                                // merge fn
                                (mappedProds, totalOrderPrice) -> GetOrderResponse.builder()
                                        .id(orderServiceResponse.orderId())
                                        .currency(PriceCurrency.RUB)
                                        .products(mappedProds)
                                        .totalPrice(totalOrderPrice.setScale(2, RoundingMode.HALF_UP))
                                        .build()
                        ));
    }

    Map<String, List<UUID>> firstOptionUsingStreamWithGroupBy(List<Ord> ords) {
        return ords.stream()
                .flatMap(ord -> ord.prods().stream()
                        .map(it -> Pair.of(it.name(), ord.id()))
                )
                .collect(Collectors.groupingBy(
                        Pair::getKey,
                        Collectors.mapping(Pair::getValue, Collectors.toList())
                ));
    }

    Map<String, List<UUID>> secondOptionUsingStreamWithToMap(List<Ord> ords) {
        return ords.stream()
                .flatMap(ord -> ord.prods().stream().map(it -> Pair.of(it.name(), ord.id())))
                .collect(Collectors.toMap(
                        Pair::getKey,
                        entry -> new ArrayList<>(List.of(entry.getValue())),
                        (oldList, newList) -> {
                            oldList.addAll(newList);
                            return oldList;
                        }
                ));
    }

    private List<Ord> generateOrdsByLength(int length, int ordProdsLength) {
        return Instancio.ofList(Ord.class)
                .size(length)
                .generate(Select.field(Ord.class, "prods"), gen -> gen.collection().size(ordProdsLength))
                .generate(Select.field(Prod.class, "name"), gen -> gen.string().length(1))
                .create();
    }
}
