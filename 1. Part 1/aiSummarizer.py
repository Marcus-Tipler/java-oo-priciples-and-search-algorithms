#!/usr/bin/env python3
"""
Collect source files (Python, HTML, JavaScript, CSS) from the current
directory tree and write them – together with a tiny summary – to a
single Markdown document.

Each file appears as

    ## relative/path/to/file.ext

    | Property      | Value                         |
    |---------------|------------------------------|
    | Type          | Python / HTML / …            |
    | Size (bytes)  | 1234                         |
    | Lines         | 56                           |
    | First line    | <first non‑blank line>       |

    ```<lang>
    <file contents>
    ```
"""

from __future__ import annotations

import argparse
import pathlib
import sys
from typing import Dict, List, Tuple

# ----------------------------------------------------------------------
# Configuration – you can tweak these constants if you want
# ----------------------------------------------------------------------
# Extensions we care about (lower‑case, no leading dot)
EXTENSIONS = {".py", ".html", ".htm", ".js", ".css", ".java"}

# Mapping from extension → fence language for Markdown
FENCE_LANG: Dict[str, str] = {
    ".py": "python",
    ".html": "html",
    ".htm": "html",
    ".js": "javascript",
    ".css": "css",
    ".java": "java"
}

# Directory name fragments that should be ignored.  The check is
# case‑insensitive and matches *any* part that contains the fragment.
IGNORE_DIRS = {"venv", ".venv", "node_modules", "__pycache__"}


# ----------------------------------------------------------------------
# Helper functions
# ----------------------------------------------------------------------
def is_ignored(p: pathlib.Path) -> bool:
    """
    Return ``True`` if *any* component of ``p`` contains (case‑insensitively)
    one of the strings listed in ``IGNORE_DIRS``.
    """
    lowered_parts = (part.lower() for part in p.parts)
    for part in lowered_parts:
        for ignored in IGNORE_DIRS:
            if ignored in part:        # substring match → ignore
                return True
    return False


def find_files(root: pathlib.Path) -> List[pathlib.Path]:
    """Return a sorted list of files under ``root`` that match ``EXTENSIONS``."""
    files = [
        p
        for p in root.rglob("*")
        if p.is_file()
        and p.suffix.lower() in EXTENSIONS
        and not is_ignored(p)
    ]
    return sorted(files)


def first_meaningful_line(text: str) -> str:
    """
    Return the first non‑blank line that isn’t just a comment delimiter.
    If the file is empty return ``<empty>``.
    """
    for line in text.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        # Trim common comment prefixes so the table looks cleaner
        for prefix in ("#", "//", "<!--", "-->"):
            if stripped.startswith(prefix):
                stripped = stripped[len(prefix) :].strip()
        return stripped or "<blank>"
    return "<empty>"


def summarize(p: pathlib.Path) -> Tuple[str, List[Tuple[str, str]], str]:
    """
    Build a small summary for ``p``.
    Returns a tuple ``(type_name, rows, content)`` where *rows* is
    a list of ``(header, value)`` pairs ready to be rendered as a
    Markdown table, and *content* is the file’s raw text.
    """
    suffix = p.suffix.lower()
    type_name = {
        ".py": "Python",
        ".html": "HTML",
        ".htm": "HTML",
        ".js": "JavaScript",
        ".css": "CSS",
    }.get(suffix, "Unknown")

    # Read the whole file once – we need it for the fence anyway.
    # Errors fall back to an empty string so the script never crashes.
    try:
        content = p.read_text(encoding="utf-8")
    except Exception as exc:  # pragma: no cover
        content = ""
        sys.stderr.write(f"⚠️  Could not read {p}: {exc}\n")

    size = p.stat().st_size
    lines = content.count("\n") + (0 if content.endswith("\n") else 1)

    rows = [
        ("Type", type_name),
        ("Size (bytes)", str(size)),
        ("Lines", str(lines)),
        ("First line", first_meaningful_line(content).replace("|", r"\|")),
    ]
    return type_name, rows, content


def markdown_table(rows: List[Tuple[str, str]]) -> str:
    """Render ``rows`` as a simple GitHub‑flavoured Markdown table."""
    lines = ["| Property | Value |", "|---|---|"]
    for key, value in rows:
        lines.append(f"| {key} | {value} |")
    return "\n".join(lines) + "\n"


# ----------------------------------------------------------------------
# Core routine
# ----------------------------------------------------------------------
def build_markdown(
    out_path: pathlib.Path = pathlib.Path("ai_sum.md"),
    root: pathlib.Path = pathlib.Path.cwd(),
) -> None:
    """Collect source files and write the markdown document."""
    out_path.parent.mkdir(parents=True, exist_ok=True)

    files = find_files(root)

    with out_path.open("w", encoding="utf-8") as md:
        md.write("# Source‑code dump\n\n")
        for p in files:
            rel_path = p.relative_to(root)

            # ---- heading -------------------------------------------------
            md.write(f"## {rel_path}\n\n")

            # ---- summary -------------------------------------------------
            _, summary_rows, content = summarize(p)
            md.write(markdown_table(summary_rows))
            md.write("\n")

            # ---- fenced source -------------------------------------------
            fence = FENCE_LANG.get(p.suffix.lower(), "")
            md.write(f"```{fence}\n")
            md.write(content.rstrip("\n"))  # keep original line endings
            md.write("\n```\n\n")


# ----------------------------------------------------------------------
# CLI entry point
# ----------------------------------------------------------------------
def parse_cli() -> pathlib.Path:
    parser = argparse.ArgumentParser(
        description=(
            "Collect *.py, *.html, *.js and *.css files into a single markdown file. "
            "Directories whose name contains 'venv' (e.g. venv, my-venv) are ignored."
        ),
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "output",
        nargs="?",
        default="ai_sum.md",
        help="Path of the markdown file to create (will be overwritten).",
    )
    args = parser.parse_args()
    return pathlib.Path(args.output)


if __name__ == "__main__":
    output_path = parse_cli()
    build_markdown(output_path)
