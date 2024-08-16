
/**
 * TODO http only cookie 서버쪽에서 생성.
 * async function saveTokenInCookie(token) {
 *   await fetch('/api/set-token-cookie', {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json' },
 *     body: JSON.stringify({ token })
 *   });
 * }
 * @GetMapping("/set-cookie")
 * public ResponseEntity<?> setCookie(HttpServletResponse response) {
 *     ResponseCookie cookie = ResponseCookie.from("myCookie", "myValue")
 *             .httpOnly(true)
 *             .secure(true)
 *             .sameSite("Strict")
 *             .maxAge(Duration.ofDays(30))
 *             .path("/")
 *             .build();
 *
 *     response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
 *     return ResponseEntity.ok().build();
 * }
 */
