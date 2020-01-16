import '../../support/embed/commands.js'
import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Keyboard button visibility test', () => {
    beforeEach(() => {
        cy.visit('classic.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Keyboard button shouldn't be shown when an input lost the focus",
    () => {
        cy.writeInAVInput("f(x)=x");
        console.log(selectors)
        cy.keyboardShouldPresent();
        selectors.euclidianView.get()
                    .mouseEvent('down', 100, 100)
                    .mouseEvent('up', 100, 100);

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('not.be.visible');
    });

    it("Keyboard button shouldn't be shown after the keyboard is closed with X",
    () => {
        cy.writeInAVInput("g(x)=x");

        selectors.closeKeyboardButton.get().should('be.visible');
        selectors.closeKeyboardButton.get().click()
        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('not.be.visible');
    });

    it("Keyboard button should be visible when input gains focus and keyboard was closed earlier",
    () => {
        cy.writeInAVInput("g(x)=x");
        selectors.closeKeyboardButton.get().click()

        selectors.euclidianView.get()
                       .mouseEvent('down', 100, 100)
                       .mouseEvent('up', 100, 100);

        cy.writeInAVInput("h(x)=x");

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('be.visible');
        selectors.showKeyboardButton.get().click();
        cy.keyboardShouldPresent();
    });
})
