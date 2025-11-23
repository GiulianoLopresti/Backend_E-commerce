package com.looprex.shopping.config;

import com.looprex.shopping.model.Buy;
import com.looprex.shopping.model.Detail;
import com.looprex.shopping.repository.BuyRepository;
import com.looprex.shopping.repository.DetailRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BuyRepository buyRepository;
    private final DetailRepository detailRepository;

    public DataSeeder(BuyRepository buyRepository, DetailRepository detailRepository) {
        this.buyRepository = buyRepository;
        this.detailRepository = detailRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Evitar duplicados
        if (buyRepository.count() == 0) {
            // Compra 1
            Buy buy1 = new Buy();
            buy1.setOrderNumber("ORD-2025-001");
            buy1.setBuyDate(System.currentTimeMillis() - 86400000L);
            buy1.setSubtotal(1899990);
            buy1.setIva(361098);
            buy1.setShipping(5990);
            buy1.setTotal(2267078);
            buy1.setPaymentMethod("Tarjeta de Débito");
            buy1.setStatusId(4L); 
            buy1.setAddressId(1L);
            buy1.setUserId(1L);
            buyRepository.save(buy1);

            // Detalle Compra 1
            Detail detail1 = new Detail();
            detail1.setBuyId(buy1.getBuyId()); // Usar ID generado es más seguro
            detail1.setProductId(1L);
            detail1.setQuantity(1);
            detail1.setUnitPrice(1899990);
            detail1.setSubtotal(1899990);
            detailRepository.save(detail1);

            // Compra 2
            Buy buy2 = new Buy();
            buy2.setOrderNumber("ORD-2025-002");
            buy2.setBuyDate(System.currentTimeMillis() - 172800000L);
            buy2.setSubtotal(729980);
            buy2.setIva(138696);
            buy2.setShipping(5990);
            buy2.setTotal(874666);
            buy2.setPaymentMethod("Tarjeta de Crédito");
            buy2.setStatusId(2L);
            buy2.setAddressId(2L);
            buy2.setUserId(2L);
            buyRepository.save(buy2);

            // Detalles Compra 2
            Detail detail2 = new Detail();
            detail2.setBuyId(buy2.getBuyId());
            detail2.setProductId(2L);
            detail2.setQuantity(2);
            detail2.setUnitPrice(129990);
            detail2.setSubtotal(259980);
            detailRepository.save(detail2);

            Detail detail3 = new Detail();
            detail3.setBuyId(buy2.getBuyId());
            detail3.setProductId(6L);
            detail3.setQuantity(1);
            detail3.setUnitPrice(699990);
            detail3.setSubtotal(699990);
            detailRepository.save(detail3);

        }
    }
}