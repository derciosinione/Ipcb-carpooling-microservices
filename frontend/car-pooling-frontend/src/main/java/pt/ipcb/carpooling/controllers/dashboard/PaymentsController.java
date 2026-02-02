package pt.ipcb.carpooling.controllers.dashboard;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pt.ipcb.carpooling.services.DashboardService;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class PaymentsController {
    private final DashboardService dashboardService;

    @GetMapping("/payments")
    public String payments(@RequestParam(name = "period", defaultValue = "month") String period,
            Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        List<DriverPaymentDto> driverPayments = List.of();
        List<PassengerPaymentDto> passengerPayments = List.of();

        try {
            driverPayments = dashboardService.buildDriverPayments(user.getId());
            driverPayments = dashboardService.filterDriverPaymentsByPeriod(driverPayments, period);
        } catch (Exception e) {
            log.error("Error loading driver payments for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("error", "Erro ao carregar pagamentos do condutor.");
        }

        try {
            passengerPayments = dashboardService.buildPassengerPayments(user.getId());
            passengerPayments = dashboardService.filterPassengerPaymentsByPeriod(passengerPayments, period);
        } catch (Exception e) {
            log.error("Error loading passenger payments for user {}: {}", user.getId(), e.getMessage());
            if (!model.containsAttribute("error")) {
                model.addAttribute("error", "Erro ao carregar pagamentos do passageiro.");
            }
        }

        model.addAttribute("period", dashboardService.normalizePeriod(period));
        model.addAttribute("driverPayments", driverPayments);
        model.addAttribute("passengerPayments", passengerPayments);
        model.addAttribute("driverCollectedTotal", dashboardService.sumDriverCollected(driverPayments));
        model.addAttribute("driverPendingTotal", dashboardService.sumDriverPending(driverPayments));
        model.addAttribute("passengerPaidTotal", dashboardService.sumPassengerPaid(passengerPayments));
        model.addAttribute("passengerPendingTotal", dashboardService.sumPassengerPending(passengerPayments));
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
        String normalizedPeriod = dashboardService.normalizePeriod(period);
        String csv;

        if ("driver".equals(normalizedRole)) {
            List<DriverPaymentDto> items = dashboardService.filterDriverPaymentsByPeriod(
                    dashboardService.buildDriverPayments(user.getId()),
                    normalizedPeriod);
            csv = dashboardService.buildDriverCsv(items);
        } else {
            List<PassengerPaymentDto> items = dashboardService.filterPassengerPaymentsByPeriod(
                    dashboardService.buildPassengerPayments(user.getId()),
                    normalizedPeriod);
            csv = dashboardService.buildPassengerCsv(items);
        }

        String fileName = "pagamentos-" + normalizedRole + "-" + normalizedPeriod + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}