// Extracted from app.ts so it can be unit-tested without pulling in the
// DOM-dependent setup*() calls app.ts runs at module load.
export function pathParams(path: string): string[] {
  return [...path.matchAll(/\{([^}]+)\}/g)].map((m) => m[1]);
}
