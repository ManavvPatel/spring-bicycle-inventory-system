/**
 * Run against a fresh disposable demo ONLY; this creates shop records.
 * npm install --no-save playwright && npx playwright install chromium
 * Start the demo, then: node scripts/browser-smoke.cjs
 * Optional: BASE_URL, PLAYWRIGHT_CHROMIUM_EXECUTABLE, PLAYWRIGHT_MODULE_PATH.
 */
const assert = require("node:assert/strict");
const path = require("node:path");
const fs = require("node:fs");
const { chromium } = require(
  process.env.PLAYWRIGHT_MODULE_PATH || "playwright",
);
const base = process.env.BASE_URL || "http://127.0.0.1:8080";
const screenshots = path.join(__dirname, "..", "docs", "screenshots");
const errors = [];
const executablePath = process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE;
(async () => {
  fs.mkdirSync(screenshots, { recursive: true });
  const browser = await chromium.launch({
    headless: true,
    ...(executablePath ? { executablePath } : {}),
  });
  try {
    const page = await browser.newPage({
      viewport: { width: 1440, height: 1080 },
    });
    page.on("pageerror", (error) => errors.push(error.message));
    page.on("response", (response) => {
      if (response.status() >= 500)
        errors.push(`${response.status()} ${response.url()}`);
    });
    await page.goto(base + "/login");
    assert.equal(
      await page.locator(".demo-credentials").count(),
      1,
      "Refusing to mutate a non-demo application",
    );
    await page.screenshot({
      path: path.join(screenshots, "login.png"),
      fullPage: true,
    });
    const login = async (username, password) => {
      await page.getByLabel("Username", { exact: true }).fill(username);
      await page.getByLabel("Password", { exact: true }).fill(password);
      await page.getByRole("button", { name: "Sign in to workspace" }).click();
      await page.waitForURL("**/home");
    };
    await login("owner", "owner123");
    await page.screenshot({
      path: path.join(screenshots, "overview.png"),
      fullPage: true,
    });
    assert.match(await page.locator("h1").innerText(), /Shop overview/);
    await page.goto(base + "/products");
    await page.getByLabel("Search this table").fill("Urban Helmet");
    assert.equal(await page.locator("tbody tr[data-row]:visible").count(), 1);
    await page.getByLabel("Search this table").fill("no-such-product");
    assert.equal(await page.locator("[data-no-results]:visible").count(), 1);
    await page.getByLabel("Search this table").fill("");
    await page.screenshot({
      path: path.join(screenshots, "products.png"),
      fullPage: true,
    });
    await page.goto(base + "/sales/add");
    await page
      .getByLabel("Customer", { exact: true })
      .selectOption({ index: 1 });
    await page.locator(".product-line input[type=checkbox]").first().check();
    await page.locator(".product-line input[type=number]").first().fill("2");
    await page.getByRole("button", { name: "Complete sale" }).click();
    await page.waitForURL("**/sales");
    assert.match(
      await page.locator(".alert-success").innerText(),
      /Sale recorded/,
    );
    await page.goto(base + "/inventory");
    const getStock = async () =>
      Number(
        await page
          .locator("tbody tr[data-row]")
          .filter({ hasText: "Ridgeway 29" })
          .locator("td")
          .nth(1)
          .innerText(),
      );
    assert.equal(await getStock(), 6);
    await page.goto(base + "/returns/add");
    await page.getByLabel("Sale:", { exact: true }).selectOption({ index: 1 });
    await page
      .getByLabel("Product Returned:", { exact: true })
      .selectOption({ index: 1 });
    await page.getByRole("button", { name: "Submit Return" }).click();
    await page.waitForURL("**/returns");
    assert.match(await page.locator("tbody").innerText(), /1,299.00/);
    await page.goto(base + "/orders/add");
    await page
      .getByLabel("Supplier", { exact: true })
      .selectOption({ index: 1 });
    await page.locator(".product-line input[type=checkbox]").first().check();
    await page.locator(".product-line input[type=number]").first().fill("3");
    await page.getByRole("button", { name: "Create order" }).click();
    await page.waitForURL("**/orders");
    await page
      .locator("select[name=statusID]")
      .first()
      .selectOption({ label: "Received" });
    await page
      .getByRole("button", { name: "Update", exact: true })
      .first()
      .click();
    await page.waitForURL("**/orders");
    assert.equal(await page.locator("select[name=statusID]").count(), 0);
    await page.goto(base + "/inventory");
    assert.equal(await getStock(), 10);
    await page.goto(base + "/serviceTransactions/add");
    await page
      .getByLabel("Customer:", { exact: true })
      .selectOption({ index: 1 });
    await page
      .getByLabel("Service:", { exact: true })
      .selectOption({ index: 1 });
    await page.getByRole("button", { name: "Submit Transaction" }).click();
    await page.waitForURL("**/serviceTransactions");
    assert.match(await page.locator("tbody").innerText(), /Essential tune-up/);
    await page.setViewportSize({ width: 390, height: 844 });
    for (const route of ["/home", "/products", "/sales/add", "/reports"]) {
      await page.goto(base + route);
      assert.equal(
        await page.evaluate(
          () => document.documentElement.scrollWidth <= window.innerWidth,
        ),
        true,
        `Overflow on ${route}`,
      );
    }
    await page.goto(base + "/home");
    await page.screenshot({
      path: path.join(screenshots, "mobile-overview.png"),
      fullPage: true,
    });
    await page.getByRole("button", { name: "Menu", exact: false }).click();
    assert.equal(
      await page.locator(".menu-toggle").getAttribute("aria-expanded"),
      "true",
    );
    await page.keyboard.press("Escape");
    assert.equal(
      await page.locator(".menu-toggle").getAttribute("aria-expanded"),
      "false",
    );
    await page.setViewportSize({ width: 1440, height: 1080 });
    await page.getByRole("button", { name: "Sign out", exact: true }).click();
    await page.waitForURL("**/login?logout");
    await login("employee", "employee123");
    assert.equal(await page.locator('nav a[href="/orders"]').count(), 0);
    assert.equal(await page.locator('nav a[href="/admin/users"]').count(), 0);
    assert.equal((await page.goto(base + "/orders")).status(), 403);
    assert.equal(errors.length, 0, errors.join("\n"));
    console.log(
      "PASS: login, table search, sale, stock, return, order receipt, service record, mobile layout/menu, logout and role visibility.",
    );
  } finally {
    await browser.close();
  }
})().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
