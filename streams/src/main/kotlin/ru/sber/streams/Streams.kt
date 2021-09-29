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
    val numberOfDeliveredProductByCity = mutableMapOf<City, Int>()
    for (customer in customers) {
        var amountOfProduct = customer.orders.stream().filter { it.isDelivered }.map { it.products.size }.toList().sum()
        if (numberOfDeliveredProductByCity.containsKey(customer.city))
            amountOfProduct += numberOfDeliveredProductByCity[customer.city] ?: 0
        numberOfDeliveredProductByCity[customer.city] = amountOfProduct
    }
    return numberOfDeliveredProductByCity
}

// 8. Получить соответствие в мапе: город - самый популярный продукт в городе.
fun Shop.getMostPopularProductInCity(): Map<City, Product> {
    val allProductsOfEachCity = mutableMapOf<City, List<Product>>()
    for (customer in customers) {
        var amountOfProducts = customer.orders.flatMap { it.products }.toList()
        if (allProductsOfEachCity.containsKey(customer.city))
            amountOfProducts += (allProductsOfEachCity[customer.city]!!)
        allProductsOfEachCity[customer.city] = amountOfProducts
    }
    val popularProductOfEachCity = mutableMapOf<City, Product>()
    for ((k, v) in allProductsOfEachCity) {
        val popularProduct = v.groupingBy { it.name }.eachCount().maxByOrNull { it.value }
        if (popularProduct != null)
            popularProductOfEachCity[k] = v.find { it.name == popularProduct.key }!!
    }
    return popularProductOfEachCity
}

// 9. Получить набор товаров, которые заказывали все покупатели.
fun Shop.getProductsOrderedByAll(): Set<Product> {
    val allProducts = customers.flatMap { it.orders }.flatMap { it.products }.toSet()
    val productsOrderedByAll = mutableSetOf<Product>()
    for (product in allProducts) {
        var hasProduct = true
        for (customer in customers) {
            if (product !in customer.orders.flatMap { it.products }.toSet()) {
                hasProduct = false
                break
            }
        }
        if (hasProduct)
            productsOrderedByAll.add(product)
    }
    return productsOrderedByAll
}

