package com.jaya.expenseservice.service.cashflow;

import com.jaya.expenseservice.dto.ExpenseDTO;
import com.jaya.expenseservice.dto.ExpenseDetailsDTO;
import com.jaya.expenseservice.dto.cashflow.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashflowAggregationService {

    private static final List<String> WEEK_DAYS = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
    private static final DateTimeFormatter MONTH_SHORT = DateTimeFormatter.ofPattern("MMM");
    private static final DateTimeFormatter DAY_NUMERIC = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter RANGE_LABEL = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static final DateTimeFormatter CARD_MONTH_LABEL = DateTimeFormatter.ofPattern("MMMM yyyy");

    public CashflowDashboardResponse buildDashboardResponse(
            List<ExpenseDTO> ExpenseDTOs,
            String requestedRange,
            Integer offset,
            LocalDate startDate,
            LocalDate endDate,
            String flowType,
            String searchTerm) {

        List<ExpenseDTO> safeExpenseDTOs = ExpenseDTOs == null ? Collections.emptyList() : new ArrayList<>(ExpenseDTOs);
        List<ExpenseDTO> filteredExpenseDTOs = filterExpensesBySearchTerm(safeExpenseDTOs, searchTerm);
        String normalizedRange = normalizeRange(requestedRange, startDate, endDate);

        CashflowDashboardResponse response = new CashflowDashboardResponse();
        response.setRawExpenses(filteredExpenseDTOs);
        response.setCardData(buildCardData(filteredExpenseDTOs));
        response.setChartData(
                buildChartData(filteredExpenseDTOs, normalizedRange, startDate, endDate));
        response.setTotals(calculateTotals(filteredExpenseDTOs));
        response.setRangeContext(
                buildRangeContext(normalizedRange, offset, startDate, endDate, flowType, searchTerm));
        response.setXKey(resolveXKey(normalizedRange));
        return response;
    }

    public List<ExpenseDTO> filterExpensesBySearchTerm(
            List<ExpenseDTO> ExpenseDTOs, String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return ExpenseDTOs;
        }
        String normalized = searchTerm.trim().toLowerCase(Locale.ENGLISH);
        return ExpenseDTOs.stream()
                .filter(dto -> matchesSearch(dto, normalized))
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(ExpenseDTO dto, String searchTerm) {
        if (dto == null || searchTerm == null || searchTerm.isBlank()) {
            return true;
        }
        ExpenseDetailsDTO details = dto.getExpense();
        return contains(details != null ? details.getExpenseName() : null, searchTerm)
                || contains(details != null ? details.getComments() : null, searchTerm)
                || contains(details != null ? details.getPaymentMethod() : null, searchTerm)
                || contains(dto.getCategoryName(), searchTerm)
                || contains(dto.getDate(), searchTerm)
                || contains(details != null ? details.getType() : null, searchTerm);
    }

    private boolean contains(String value, String term) {
        return value != null && value.toLowerCase(Locale.ENGLISH).contains(term);
    }

    private String normalizeRange(String requestedRange, LocalDate start, LocalDate end) {
        if (requestedRange != null && !requestedRange.isBlank()) {
            return requestedRange.toLowerCase(Locale.ENGLISH);
        }
        if (start != null && end != null) {
            if (ChronoUnit.DAYS.between(start, end) <= 6) {
                return "week";
            }
            if (start.getDayOfMonth() == 1 && end.equals(start.plusMonths(1).minusDays(1))) {
                return "month";
            }
            if (start.getDayOfYear() == 1 && end.equals(start.plusYears(1).minusDays(1))) {
                return "year";
            }
        }
        return "custom";
    }

    private List<CashflowCardDTO> buildCardData(List<ExpenseDTO> ExpenseDTOs) {
        List<CashflowCardDTO> cards = new ArrayList<>();
        for (ExpenseDTO dto : ExpenseDTOs) {
            CashflowCardDTO card = new CashflowCardDTO();
            BeanUtils.copyProperties(dto, card);
            card.setIsoDate(dto.getDate());
            ExpenseDetailsDTO details = dto.getExpense();
            double amount = details != null ? details.getAmountAsDouble() : 0.0;
            card.setAmount(amount);
            card.setName(details != null ? details.getExpenseName() : null);
            card.setComments(details != null ? details.getComments() : null);
            LocalDate date = safeParse(dto.getDate());
            if (date != null) {
                card.setDayLabel(DAY_NUMERIC.format(date));
                card.setMonthLabel(CARD_MONTH_LABEL.format(date));
                card.setWeekLabel("Week " + date.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
                card.setBucketLabel(date.format(RANGE_LABEL));
            }
            cards.add(card);
        }
        return cards;
    }

    private List<CashflowChartBucketDTO> buildChartData(
            List<ExpenseDTO> ExpenseDTOs,
            String range,
            LocalDate startDate,
            LocalDate endDate) {
        switch (range) {
            case "week":
                return buildWeeklyChart(ExpenseDTOs, startDate);
            case "month":
                return buildMonthlyChart(ExpenseDTOs, startDate);
            case "year":
                return buildYearlyChart(ExpenseDTOs, startDate);
            default:
                return buildCustomChart(ExpenseDTOs, startDate, endDate);
        }
    }

    private List<CashflowChartBucketDTO> buildWeeklyChart(
            List<ExpenseDTO> ExpenseDTOs, LocalDate start) {
        if (start == null) {
            return Collections.emptyList();
        }
        List<CashflowChartBucketDTO> buckets = new ArrayList<>();
        for (int i = 0; i < WEEK_DAYS.size(); i++) {
            LocalDate bucketDate = start.plusDays(i);
            CashflowChartBucketDTO bucket = new CashflowChartBucketDTO();
            bucket.setDay(WEEK_DAYS.get(i));
            bucket.setLabel(bucketDate.format(RANGE_LABEL));
            bucket.setIsoDate(bucketDate.toString());
            buckets.add(bucket);
        }
        for (ExpenseDTO dto : ExpenseDTOs) {
            LocalDate date = safeParse(dto.getDate());
            if (date == null) {
                continue;
            }
            long diff = ChronoUnit.DAYS.between(start, date);
            if (diff < 0 || diff >= buckets.size()) {
                continue;
            }
            buckets.get((int) diff).addExpense(dto, resolveAmount(dto));
        }
        return buckets;
    }

    private List<CashflowChartBucketDTO> buildMonthlyChart(
            List<ExpenseDTO> ExpenseDTOs, LocalDate start) {
        if (start == null) {
            return Collections.emptyList();
        }
        int daysInMonth = start.lengthOfMonth();
        List<CashflowChartBucketDTO> buckets = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate bucketDate = start.withDayOfMonth(i);
            CashflowChartBucketDTO bucket = new CashflowChartBucketDTO();
            bucket.setDay(String.valueOf(i));
            bucket.setLabel(bucketDate.format(RANGE_LABEL));
            bucket.setIsoDate(bucketDate.toString());
            buckets.add(bucket);
        }
        for (ExpenseDTO dto : ExpenseDTOs) {
            LocalDate date = safeParse(dto.getDate());
            if (date == null || date.getMonthValue() != start.getMonthValue()
                    || date.getYear() != start.getYear()) {
                continue;
            }
            int index = date.getDayOfMonth() - 1;
            buckets.get(index).addExpense(dto, resolveAmount(dto));
        }
        return buckets;
    }

    private List<CashflowChartBucketDTO> buildYearlyChart(
            List<ExpenseDTO> ExpenseDTOs, LocalDate start) {
        if (start == null) {
            return Collections.emptyList();
        }
        List<CashflowChartBucketDTO> buckets = new ArrayList<>();
        LocalDate base = start.withDayOfYear(1);
        for (int i = 0; i < 12; i++) {
            LocalDate bucketDate = base.withMonth(i + 1);
            CashflowChartBucketDTO bucket = new CashflowChartBucketDTO();
            bucket.setMonth(MONTH_SHORT.format(bucketDate));
            bucket.setLabel(bucketDate.getMonth().name());
            bucket.setIsoDate(bucketDate.toString());
            buckets.add(bucket);
        }
        for (ExpenseDTO dto : ExpenseDTOs) {
            LocalDate date = safeParse(dto.getDate());
            if (date == null || date.getYear() != base.getYear()) {
                continue;
            }
            int index = date.getMonthValue() - 1;
            buckets.get(index).addExpense(dto, resolveAmount(dto));
        }
        return buckets;
    }

    private List<CashflowChartBucketDTO> buildCustomChart(
            List<ExpenseDTO> ExpenseDTOs, LocalDate start, LocalDate end) {
        Map<LocalDate, CashflowChartBucketDTO> buckets = new LinkedHashMap<>();
        LocalDate cursor = start;
        while (cursor != null && end != null && !cursor.isAfter(end)) {
            CashflowChartBucketDTO bucket = new CashflowChartBucketDTO();
            bucket.setDay(DAY_NUMERIC.format(cursor));
            bucket.setMonth(MONTH_SHORT.format(cursor));
            bucket.setLabel(cursor.format(RANGE_LABEL));
            bucket.setIsoDate(cursor.toString());
            buckets.put(cursor, bucket);
            cursor = cursor.plusDays(1);
        }
        for (ExpenseDTO dto : ExpenseDTOs) {
            LocalDate date = safeParse(dto.getDate());
            if (date == null) {
                continue;
            }
            CashflowChartBucketDTO bucket = buckets.computeIfAbsent(date, d -> {
                CashflowChartBucketDTO newBucket = new CashflowChartBucketDTO();
                newBucket.setDay(DAY_NUMERIC.format(d));
                newBucket.setMonth(MONTH_SHORT.format(d));
                newBucket.setLabel(d.format(RANGE_LABEL));
                newBucket.setIsoDate(d.toString());
                return newBucket;
            });
            bucket.addExpense(dto, resolveAmount(dto));
        }
        return new ArrayList<>(buckets.values());
    }

    private CashflowTotalsDTO calculateTotals(List<ExpenseDTO> ExpenseDTOs) {
        double inflow = 0;
        double outflow = 0;
        for (ExpenseDTO dto : ExpenseDTOs) {
            ExpenseDetailsDTO details = dto.getExpense();
            if (details == null) {
                continue;
            }
            double amount = details.getAmountAsDouble();
            String type = details.getType() == null ? "outflow" : details.getType().toLowerCase(Locale.ENGLISH);
            if (Objects.equals(type, "gain") || Objects.equals(type, "inflow")) {
                inflow += amount;
            } else {
                outflow += amount;
            }
        }
        return new CashflowTotalsDTO(inflow, outflow, inflow + outflow);
    }

    private CashflowRangeContextDTO buildRangeContext(
            String range,
            Integer offset,
            LocalDate start,
            LocalDate end,
            String flowType,
            String searchTerm) {
        CashflowRangeContextDTO context = new CashflowRangeContextDTO();
        context.setRangeType(range);
        context.setOffset(offset);
        context.setStartDate(start != null ? start.toString() : null);
        context.setEndDate(end != null ? end.toString() : null);
        context.setFlowType(flowType);
        context.setSearch(
                searchTerm == null ? null : searchTerm.trim().toLowerCase(Locale.ENGLISH));
        context.setLabel(buildRangeLabel(range, offset, start, end));
        return context;
    }

    private String buildRangeLabel(String range, Integer offset, LocalDate start, LocalDate end) {
        if ("month".equals(range) && start != null && end != null) {
            if (offset == null || offset == 0) {
                LocalDate now = LocalDate.now();
                return "This Month (" + MONTH_SHORT.format(now) + " " + now.getYear() + ")";
            }
            return RANGE_LABEL.format(start) + " - " + RANGE_LABEL.format(end);
        }
        if ("week".equals(range) && start != null && end != null) {
            return RANGE_LABEL.format(start) + " - " + RANGE_LABEL.format(end);
        }
        if ("year".equals(range) && start != null) {
            if (offset == null || offset == 0) {
                return "This Year";
            }
            return String.valueOf(start.getYear());
        }
        if (start != null && end != null) {
            return RANGE_LABEL.format(start) + " - " + RANGE_LABEL.format(end);
        }
        return "";
    }

    private String resolveXKey(String range) {
        if ("year".equals(range)) {
            return "month";
        }
        if ("week".equals(range) || "month".equals(range)) {
            return "day";
        }
        return "label";
    }

    private LocalDate safeParse(String date) {
        try {
            return date == null ? null : LocalDate.parse(date);
        } catch (Exception ex) {
            return null;
        }
    }

    private double resolveAmount(ExpenseDTO dto) {
        ExpenseDetailsDTO details = dto.getExpense();
        return details != null ? details.getAmountAsDouble() : 0.0;
    }
}
