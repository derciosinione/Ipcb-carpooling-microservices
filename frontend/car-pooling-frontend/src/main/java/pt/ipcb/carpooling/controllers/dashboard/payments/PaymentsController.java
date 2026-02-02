package pt.ipcb.carpooling.controllers.dashboard.payments;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.DriverPaymentDto;
import pt.ipcb.carpooling.dto.PassengerPaymentDto;
import pt.ipcb.carpooling.services.payments.PaymentsDashboardService;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentsDashboardService paymentsDashboardService;

    @GetMapping("/payments")
    public String payments(@RequestParam(name = "period", defaultValue = "month") String period,
            Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        List<DriverPaymentDto> driverPayments = paymentsDashboardService.buildDriverPayments(user.getId());
        driverPayments = paymentsDashboardService.filterDriverPaymentsByPeriod(driverPayments, period);

        List<PassengerPaymentDto> passengerPayments = paymentsDashboardService.buildPassengerPayments(user.getId());
        passengerPayments = paymentsDashboardService.filterPassengerPaymentsByPeriod(passengerPayments, period);

        model.addAttribute("period", paymentsDashboardService.normalizePeriod(period));
        model.addAttribute("driverPayments", driverPayments);
        model.addAttribute("passengerPayments", passengerPayments);
        model.addAttribute("driverCollectedTotal", paymentsDashboardService.sumDriverCollected(driverPayments));
        model.addAttribute("driverPendingTotal", paymentsDashboardService.sumDriverPending(driverPayments));
        model.addAttribute("passengerPaidTotal", paymentsDashboardService.sumPassengerPaid(passengerPayments));
        model.addAttribute("passengerPendingTotal", paymentsDashboardService.sumPassengerPending(passengerPayments));
        return "dashboard/payments";
    }

    @GetMapping("/payments/export")
    public ResponseEntity<byte[]> exportPaymentsCsv(
            @RequestParam(name = "role", defaultValue = "passenger") String role,
            @RequestParam(name = "period", defaultValue = "month") String period,
            HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        String normalizedRole = "driver".equalsIgnoreCase(role) ? "driver" : "passenger";
        String normalizedPeriod = paymentsDashboardService.normalizePeriod(period);
        String csv;

        if ("driver".equals(normalizedRole)) {
            List<DriverPaymentDto> items = paymentsDashboardService.filterDriverPaymentsByPeriod(
                    paymentsDashboardService.buildDriverPayments(user.getId()),
                    normalizedPeriod);
            csv = paymentsDashboardService.buildDriverCsv(items);
        } else {
            List<PassengerPaymentDto> items = paymentsDashboardService.filterPassengerPaymentsByPeriod(
                    paymentsDashboardService.buildPassengerPayments(user.getId()),
                    normalizedPeriod);
            csv = paymentsDashboardService.buildPassengerCsv(items);
        }

        String fileName = "pagamentos-" + normalizedRole + "-" + normalizedPeriod + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
