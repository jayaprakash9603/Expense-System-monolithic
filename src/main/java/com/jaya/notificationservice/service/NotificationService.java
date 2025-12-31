package com.jaya.notificationservice.service;

import com.jaya.notificationservice.modal.BudgetDTO;
import com.jaya.notificationservice.modal.ExpenseDTO;
import com.jaya.notificationservice.modal.Notification;
import com.jaya.userservice.modal.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    // New method for creating notifications from events
    Notification createNotification(Notification notification);

    // New methods for notification management
    List<Notification> getUserNotifications(Integer userId, Boolean isRead, Integer limit, Integer offset);

    Notification markAsRead(Integer notificationId, Integer userId);

    void deleteNotification(Integer notificationId, Integer userId);

    void deleteAllNotifications(Integer userId);

    void markAllAsRead(Integer userId);

    Long getUnreadCount(Integer userId);

    void sendBudgetDTOExceededAlert(User User, BudgetDTO BudgetDTO, double currentSpending);

    void sendBudgetDTOWarningAlert(User User, BudgetDTO BudgetDTO, double currentSpending,
                                   double warningThreshold);

    void sendMonthlySpendingSummary(User User, LocalDate month);

    void sendDailyExpenseDTOReminder(User User);

    void sendWeeklyExpenseDTOReport(User User);

    void sendUnusualSpendingAlert(User User, ExpenseDTO ExpenseDTO);

    void sendRecurringExpenseDTOReminder(User User, List<ExpenseDTO> recurringExpenseDTOs);

    void sendGoalAchievementNotification(User User, String goalType, double targetAmount);

    void sendInactivityReminder(User User, int daysSinceLastExpenseDTO);

    void scheduleMonthlyReports(User User);

    void sendCustomAlert(User User, String message, String alertType);

    List<String> getNotificationHistory(User User, int limit);

    void sendBudgetExceededAlert(User user, BudgetDTO budget, double currentSpending);

    public void sendBudgetWarningAlert(User user, BudgetDTO budget, double currentSpending, double warningThreshold);

    public void sendDailyExpenseReminder(User user);

    public void sendWeeklyExpenseReport(User user);

    public void sendRecurringExpenseReminder(User user, List<ExpenseDTO> recurringExpenses);

    public void updateNotificationPreferences(User user, Map<String, Boolean> preferences);
    // void updateNotificationPreferences(User User, Map<String, Boolean>
    // preferences);
}