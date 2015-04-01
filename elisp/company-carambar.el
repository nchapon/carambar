(require 'url-vars)
(require 'url-dav)

(require 'json)

(defun call-carambar (s)
    "DOCSTRING"
  (interactive)
  (with-current-buffer
      (url-retrieve-synchronously (format "http://localhost:3000/classes?search=%s" s))
  (goto-char url-http-end-of-headers)
  (setq json-array-type 'vector
        json-object-type 'alist)

  (let* ((rtnval (json-read)))
    (kill-buffer (current-buffer))
    (append (cdr (assoc 'classes rtnval)) nil))))


(defun company-carambar (command &optional arg &rest ignored)
  (interactive (list 'interactive))
  (case command
    (interactive (company-begin-backend 'company-sample-backend))
    (prefix (and (eq major-mode 'java-mode)
                 (company-grab-symbol)))
    (candidates
     (call-carambar arg))))

(add-to-list 'company-backends 'company-carambar)
