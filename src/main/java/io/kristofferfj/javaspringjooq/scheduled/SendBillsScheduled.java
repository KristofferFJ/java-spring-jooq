package io.kristofferfj.javaspringjooq.scheduled;

import io.kristofferfj.javaspringjooq.domain.bill.Bill;
import io.kristofferfj.javaspringjooq.domain.bill.BillRepository;
import io.kristofferfj.javaspringjooq.service.ProcessBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class SendBillsScheduled {

    private final BillRepository billRepository;
    private final ProcessBillService processBillService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SendBillsScheduled(BillRepository billRepository,
                              ProcessBillService processBillService) {
        this.billRepository = billRepository;
        this.processBillService = processBillService;
    }

    @Scheduled(fixedDelay = 10 * 1000)
    public void enqueueBills() {
        logger.info("Fetching first bill for processing");
        Bill bill = billRepository.getBillReadyForProcessing();
        if (bill == null) {
            logger.info("Found no bill");
            return;
        }

        logger.info("Processing bill with id=" + bill.id());
        processBillService.sendBillToBmPay(bill);
    }
}
