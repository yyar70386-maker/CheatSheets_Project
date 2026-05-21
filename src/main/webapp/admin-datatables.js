$(document).ready(function() {
    $('.admin-data-table').DataTable({
        pageLength: 10,
        lengthMenu: [5, 10, 25, 50],
        order: [],
        responsive: true,
        language: {
            search: "",
            searchPlaceholder: "Search table...",
            lengthMenu: "Show _MENU_ rows",
            paginate: {
                previous: "Prev",
                next: "Next"
            }
        }
    });
});
