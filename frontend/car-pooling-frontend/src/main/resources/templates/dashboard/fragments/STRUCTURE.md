Dashboard fragments organization

Purpose: Keep fragments grouped by feature/menu to make them easy to find and maintain.

Folder layout (new):

- fragments/common
  - layout-sidebar.html
  - layout-main-wrapper.html
  - layout-content.html

- fragments/home
  - home-header.html
  - home-stats.html
  - home-upcoming.html
  - home-actions.html

- fragments/trips
  - publish-ride-step-nav.html
  - publish-ride-step1.html
  - publish-ride-step2.html
  - publish-ride-step3.html

- fragments/rides
  - ride-header.html
  - ride-requests.html
  - ride-driver.html
  - ride-map.html
  - ride-vehicle.html
  - ride-price.html
  - ride-passengers.html
  - ride-safety.html
  - ride-expense-modal.html

- fragments/vehicles
  - vehicle-list.html (placeholder)
  - vehicle-card.html (placeholder)

Archived files (moved to `fragments/archived/`):
- publish-ride-step-nav.html
- publish-ride-step1.html
- publish-ride-step2.html
- publish-ride-step3.html
- home-header.html
- home-stats.html
- home-actions.html
- home-upcoming.html
- layout-sidebar.html
- layout-main-wrapper.html
- layout-content.html
- ride-header.html
- ride-requests.html
- ride-driver.html
- ride-map.html
- ride-vehicle.html
- ride-price.html
- ride-passengers.html
- ride-expense-modal.html
- vehicle-list.html (placeholder)
- vehicle-card.html (placeholder)

Notes:
- Templates have been updated to reference fragments in their new locations (e.g. `dashboard/fragments/home/home-header`).
- Old fragment files were moved to `fragments/archived/` to avoid accidental deletion and to keep a safe backup. If you prefer, I can delete them instead.
- If you want, I can also run the app and validate the affected pages in-browser.
