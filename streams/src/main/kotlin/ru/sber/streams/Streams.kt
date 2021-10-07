package ru.sber.streams

import java.util.stream.Collectors
import kotlin.streams.toList


// 1. Используя withIndex() посчитать сумму элементов листа, индекс которых кратен 3. (нулевой индекс тоже входит)
fun getSumWithIndexDivisibleByThree(list: List<Long>): Long {
    var sum = 0L
    for ((index, value) in list.withIndex()) {
        if (index % 3 == 0) sum += value
    }
    return sum
}

// 2. Используя функцию generateSequence() создать последовательность, возвращающую числа Фибоначчи.
fun generateFibonacciSequence(): Sequence<Int> {
    return generateSequence(Pair(0, 1)) { Pair(it.second, it.first + it.second) }.map { it.first }
}

// 3. Получить города, в которых есть покупатели.
fun Shop.getCustomersCities(): Set<City> = customers.stream().map { it.city }.collect(Collectors.toSet())

// 4. Получить все когда-либо заказанные продукты.
fun Shop.allOrderedProducts(): Set<Product> = customers.flatMap { it.orders }.flatMap { it.products }.toSet()

// 5. Получить покупателя, который сделал больше всего заказов.
fun Shop.getCustomerWithMaximumNumberOfOrders(): Customer? = customers.maxByOrNull { it.orders.size }

// 6. Получить самый дорогой продукт, когда-либо приобртенный покупателем.
fun Customer.getMostExpensiveProduct(): Product? = orders.flatMap { it.products }.toList().maxByOrNull { it.price }

// 7. Получить соответствие в мапе: город - количество заказанных и доставленных продуктов в данный город.
fun Shop.getNumberOfDeliveredProductByCity(): Map<City, Int> {
    return customers.groupBy({ it.city },
        {
            it.orders.stream().filter { order -> order.isDelivered }
                .map { order -> order.products.size }.toList().sum()
        })
        .map { it.key to it.value.sum() }.toMap()
}

// 8. Получить соответствие в мапе: город - самый популярный продукт в городе.
fun Shop.getMostPopularProductInCity(): Map<City, Product> {
    return customers.groupBy { it.city }.mapValues {
        it.value.flatMap { customer -> customer.orders }.flatMap { order -> order.products }
            .groupingBy { product -> product }.eachCount()
            .maxByOrNull { it.value!! }!!.key
    }
}

// 9. Получить набор товаров, которые заказывали все покупатели.
fun Shop.getProductsOrderedByAll(): Set<Product> {
    return customers.map { it.orders.flatMap { it.products }.toSet() }
        .reduceRight { acc, set -> acc.intersect(set) }
}