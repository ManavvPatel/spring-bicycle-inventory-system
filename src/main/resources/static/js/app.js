"use strict";
const menu = document.querySelector(".menu-toggle");
const navigation = document.querySelector("#navigation");
function closeMenu() {
  navigation?.classList.remove("open");
  menu?.setAttribute("aria-expanded", "false");
}
menu?.addEventListener("click", () => {
  const open = navigation.classList.toggle("open");
  menu.setAttribute("aria-expanded", String(open));
});
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    closeMenu();
    menu?.focus();
  }
});
document.addEventListener("click", (event) => {
  if (
    navigation?.classList.contains("open") &&
    !navigation.contains(event.target) &&
    !menu.contains(event.target)
  )
    closeMenu();
});
document.querySelectorAll("form[data-confirm]").forEach((form) =>
  form.addEventListener("submit", (event) => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  }),
);
document.querySelectorAll("[data-table-search]").forEach((input) => {
  const table = document.getElementById(input.dataset.tableSearch);
  const rows = [...table.querySelectorAll("tbody tr[data-row]")];
  const empty = table.querySelector("[data-no-results]");
  const counter = document.querySelector('[data-count-for="' + table.id + '"]');
  function filter() {
    const query = input.value.toLocaleLowerCase().trim();
    let visible = 0;
    rows.forEach((row) => {
      row.hidden = !row.textContent.toLocaleLowerCase().includes(query);
      if (!row.hidden) visible++;
    });
    if (empty) empty.hidden = visible !== 0 || rows.length === 0;
    if (counter)
      counter.textContent = visible + " of " + rows.length + " records";
  }
  input.addEventListener("input", filter);
  filter();
});
document.querySelectorAll(".product-line").forEach((row) => {
  const check = row.querySelector("input[type=checkbox]"),
    quantity = row.querySelector("input[type=number]");
  const update = () => {
    quantity.required = check.checked;
    quantity.disabled = !check.checked;
  };
  check.addEventListener("change", update);
  update();
});
document
  .querySelectorAll("[data-go-back]")
  .forEach((button) => button.addEventListener("click", () => history.back()));
