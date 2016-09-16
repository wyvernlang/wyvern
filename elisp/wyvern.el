;; Emacs support for Wyvern
;; Currently this only provides syntax highlighting

(defvar wyvern-mode-hook nil)

(defvar wyvern-mode-keymap
  (let ((map (make-keymap)))
    ;; (define-key map "C-j" 'newline-and-indent)
    map)
  "Keymap for Wyvern major mode")

;;;###autoload
(add-to-list 'auto-mode-alist '("\\.wyv\\'" . wyvern-mode))
(add-to-list 'auto-mode-alist '("\\.wyt\\'" . wyvern-mode))

(defconst wyvern-keywords
  (regexp-opt '("type"
                "def"
                "import"
                "val"
                "var"
                "new"
                "resource"
                "this"
                "delegate"
                "to"
                "require"
                "metadata"
                "module"
                "comprises"
                "extends"
                "if"
                "then"
                "else"
                "objtype"
                "as"
                "instantiate"
                "tagged"
                "match"
                "default"
                "case"
                "of")
              'words))

(defconst wyvern-constants
  (regexp-opt '("true"
                "false")
               'words))

(defconst wyvern-font-lock-keywords
  `(
   (,(rx
      symbol-start "type" symbol-end
      (one-or-more space)
      (group
       (any "a-z" "A-Z")
       (zero-or-more (any "a-z" "A-Z" "0-9" "_")))
      )
    1 font-lock-type-face)

   (,(rx
      ":"
      (one-or-more space)
      (group
       (any "a-z" "A-Z")
       (zero-or-more (any "a-z" "A-Z" "0-9" "_")))
      )
    1 font-lock-type-face)

   (,(rx
      symbol-start
      (or "val" "var")
      symbol-end
      (one-or-more space)
      (group
       (any "a-z" "A-Z")
       (zero-or-more (any "a-z" "A-Z" "0-9" "_")))
      )
    1 font-lock-variable-name-face)

   (,(rx
      symbol-start
      "def"
      symbol-end
      (one-or-more space)
      (group
       (any "a-z" "A-Z")
       (zero-or-more (any "a-z" "A-Z" "0-9" "_")))
      )
    1 font-lock-function-name-face)

   (,(rx (not (any "a-z" "A-Z" "_"))
         (one-or-more (any "0-9")))
    0 font-lock-constant-face)

   (,(rx symbol-start
         "import"
         symbol-end
         (one-or-more space)
         (group
          (any "a-z" "A-Z")
          (zero-or-more (any "a-z" "A-Z" "0-9" "_")))
         ":")
    1 font-lock-builtin-face)

   (,wyvern-constants . font-lock-constant-face)

   (,wyvern-keywords . font-lock-keyword-face))

  "Highlighting expressions for Wyvern mode")

(defun wyvern-mode ()
  "Major mode for editing Wyvern files"
  (interactive)
  (kill-all-local-variables)
  (use-local-map wyvern-mode-keymap)
  (set (make-local-variable 'font-lock-defaults)
       '(wyvern-font-lock-keywords))
  (setq major-mode 'wyvern-mode)
  (setq mode-name "Wyvern")
  (run-hooks 'wyvern-mode-hook))

(provide 'wyvern-mode)
